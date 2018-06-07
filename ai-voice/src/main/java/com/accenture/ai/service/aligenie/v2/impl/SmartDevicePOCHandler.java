package com.accenture.ai.service.aligenie.v2.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.aligenie.WebSocketServiceImpl;
import com.accenture.ai.service.aligenie.v2.AligenieHandler;
import com.accenture.ai.service.article.ArticleService;
import com.accenture.ai.utils.ArticleResultContex;
import com.accenture.ai.utils.IKAnalyzerUtil;
import com.accenture.ai.utils.NumberUtil;
import com.accenture.ai.utils.SocketStatusContex;
import com.alibaba.da.coin.ide.spi.meta.ConversationRecord;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

@Service
public class SmartDevicePOCHandler implements AligenieHandler {

	private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartDevicePOCHandler.class);
	private static final String ARTICLE_DETAIL_URL = "/websocket/articleDetailFrame";

	@Autowired
	SocketStatusContex socketStatusContex;

    @Autowired
    ArticleResultContex articleResultContex;

	@Autowired
	ArticleService articleService;

    @Autowired
    private WebSocketServiceImpl webSocketServiceImpl;

	@Override
	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("SmartDevicePOCServiceImpl start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
                .toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getLiveTime() > 0 ? "" : slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String any = paramMap.get("any");
		String sequence = paramMap.get("sequence");
		String back = paramMap.get("back");
		buildResult(taskQuery, result, any,sequence,back);
		LOGGER.info("SmartDevicePOCServiceImpl end --------------");
		return result;
	}

	private void buildResult(TaskQuery taskQuery, TaskResult result, String any, String sequence, String back) {

		if (StringUtils.isEmpty(any) && StringUtils.isEmpty(sequence) && StringUtils.isEmpty(back)) {
			result.setReply("请告诉我你要干嘛，比如 天猫精灵 智能问答 请问如何填写时间成本");
			result.setResultType(ResultType.ASK_INF);
		} else if (isSecond(sequence)) {
			int index = NumberUtil.chineseNumber2Int(sequence);
			LOGGER.info("index is:" + index);
			result.setReply(getDetailAnswer(taskQuery, any, index));
			result.setResultType(ResultType.ASK_INF);
		} else if (isBack(back)) {
			String reply = buildBackReply(taskQuery);
			result.setReply(reply);
			result.setResultType(ResultType.ASK_INF);
		} else {
			try {
				String splitWords = IKAnalyzerUtil.wordSplit(any);
				LOGGER.info("分词结果:" + splitWords);
				List<String> words = Arrays.asList(splitWords.split(","));
				List<ArticleDTO> articleDTOs = getArticles(words);
				buildReply(result,any, articleDTOs);
			} catch (IOException e) {
				result.setReply("分词发生异常,请联系管理员");
				result.setResultType(ResultType.RESULT);
				LOGGER.error("Exception happend when spliting word" + e.getMessage());
			}
		}
	}

	/**
	 * @param taskQuery
	 * @return
	 */
	private String buildBackReply(TaskQuery taskQuery) {
		String reply = "沒有上一条啦";
		if(taskQuery.getConversationRecords().size() > 1){
			LOGGER.info("用户上一次输入为:" + taskQuery.getConversationRecords().get(0).getUserInputUtterance());
			if (!taskQuery.getConversationRecords().get(0).getUserInputUtterance().equals("返回上一条")) {
				reply = taskQuery.getConversationRecords().get(1).getReplyUtterance() + "_";
			} else {
				String record = taskQuery.getConversationRecords().get(0).getReplyUtterance().replaceAll("_", "");
				LOGGER.info("上一条天猫精灵输出为:" + record );
				int index = -1;
				for (int i = 0; i < taskQuery.getConversationRecords().size(); i++) {
					if (record.equals(taskQuery.getConversationRecords().get(i).getReplyUtterance())) {
						index = i;
						break;
					}
				}
				LOGGER.info("后退索引为：" + (index + 1) + "size:" + taskQuery.getConversationRecords().size());
				if(index != -1 && index + 1 <  taskQuery.getConversationRecords().size()){
					reply = taskQuery.getConversationRecords().get(index + 1).getReplyUtterance() + "_";
				}else{
					LOGGER.info("历史记录里未找到：" + record);
				}
				
			}
		}else{
			LOGGER.info("不能返回上一条因为记录大小为:" + taskQuery.getConversationRecords().size());
		}
		return reply;
	}

	private boolean isBack(String back) {
		return StringUtils.isNotBlank(back);
	}

	private void buildReply(TaskResult result,String any, List<ArticleDTO> articleDTOs) {
		if (CollectionUtils.isEmpty(articleDTOs)) {
			LOGGER.info("分词结果未在数据库查询到相关tag");
			result.setReply("抱歉没有找到你想要的内容");
			result.setResultType(ResultType.ASK_INF);
		} else if (articleDTOs.size() == 1) {
            LOGGER.info("查询到的文章，title：" + articleDTOs.get(0).getTitle() + "  url:"+articleDTOs.get(0).getUrl());
            articleResultContex.setArticles(articleDTOs);
            socketStatusContex.setTitleAndUrl(any, ARTICLE_DETAIL_URL);
            //send contex to customer client
            webSocketServiceImpl.sendContexToClient();
			result.setReply(articleDTOs.get(0).getContent());
			result.setResultType(ResultType.ASK_INF);
		} else {
			String titles = "";
			for (ArticleDTO articleDTO : articleDTOs) {
				titles += articleDTO.getTitle() + ",";
			}
			titles += "请问想选择哪一条";
			// help to redirect url by socket(list page)
            articleResultContex.setArticles(articleDTOs);
            socketStatusContex.setTitleAndUrl(any, "/websocket/articleListFrame");
            //send contex to customer client
            webSocketServiceImpl.sendContexToClient();
			result.setReply(titles);
			result.setResultType(ResultType.ASK_INF);
		}
	}

	private List<ArticleDTO> getArticles(List<String> words) {
		// help to get articles
		List<ArticleDTO> res = null;
//		ArticleDTO a = new ArticleDTO();
//		a.setTitle("公司机票怎么定");
//		a.setContent("公司机票怎么定");
//		ArticleDTO b = new ArticleDTO();
//		b.setTitle("公司有没有快消行业实施案例");
//		b.setContent("公司有没有快消行业实施案例");
//		res.add(a);
//		res.add(b);
		return res;
	}

	private String getDetailAnswer(TaskQuery taskQuery, String any, int index) {

		ConversationRecord conversationRecord = taskQuery.getConversationRecords().stream().findFirst().orElse(null);
		if (null != conversationRecord) {
			List<String> answers = Arrays.asList(conversationRecord.getReplyUtterance().split(","));
			if (index > answers.size()) {
				return "请勿捣蛋";
			}
			// TODO 
			// help to redirect url by socket(content page)
            String title = answers.get(index - 1);
            LOGGER.info("get detail answer by title:" + title);
            ArticleDTO articleDetail = getArticleDetailFromContex(title);
            LOGGER.info("the detail answer is:" + articleDetail == null ? "" : articleDetail.getContent());

			if (articleDetail != null){
                articleResultContex.setArticles(Arrays.asList(articleDetail));
                socketStatusContex.setTitleAndUrl(any, ARTICLE_DETAIL_URL);
                //send contex to customer client
                webSocketServiceImpl.sendContexToClient();

			    return articleDetail.getContent();
            }
            else{
                LOGGER.info("can not get detail answer by title:" + title);
            }

		}
		return "抱歉，未找到上下文，请重新查询";
	}

	private ArticleDTO getArticleDetailFromContex(final String title){
        ArticleDTO result = null;
        if (StringUtils.isNotEmpty(title) && articleResultContex != null && !CollectionUtils.isEmpty(articleResultContex.getArticles())){
            for(ArticleDTO articleDTO : articleResultContex.getArticles()){
                if (title.equals(articleDTO.getTitle())){
                    result = articleDTO;
                }
            }
        }

        return result;
    }

	private boolean isSecond(String sequence) {
		return StringUtils.isNotEmpty(sequence);
	}
	
}
