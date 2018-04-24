package com.accenture.ai.service.aligenie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.accenture.ai.service.article.ArticleService;
import com.accenture.ai.utils.AligenieSessionUtil;
import com.accenture.ai.utils.ArticleResultContex;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.utils.IKAnalyzerUtil;
import com.accenture.ai.utils.NumberUtil;
import com.accenture.ai.utils.SocketStatusContex;
import com.alibaba.da.coin.ide.spi.meta.ConversationRecord;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.meta.SlotEntity;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;
import com.google.gson.Gson;

@Service
public class SmartDevicePOCServiceImpl extends AbstractAligenieService {

	private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartDevicePOCServiceImpl.class);

	@Autowired
	ArticleService articleService;
    
    private static final Gson GSON = new Gson();

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
		buildAligenieSession(taskQuery,result);
		LOGGER.info("SmartDevicePOCServiceImpl end --------------");
		return result;
	}

	private void buildAligenieSession(TaskQuery taskQuery, TaskResult result) {
		String userInput = taskQuery.getUtterance();
		String deviceOutput = result.getReply();
		ConversationRecord record = new ConversationRecord();
		record.setUserInputUtterance(userInput);
		record.setReplyUtterance(deviceOutput);
		record.setTimestamp(System.currentTimeMillis());
		TaskQuery dailog =  AligenieSessionUtil.getTaskQuery(taskQuery);
		List<ConversationRecord> conversationRecords = dailog.getConversationRecords();
		if(CollectionUtils.isEmpty(conversationRecords)){
			conversationRecords = new ArrayList<ConversationRecord>();
		}
		conversationRecords.add(record);
		conversationRecords.sort((b1, b2) -> b1.getTimestamp() > b2.getTimestamp() ?  -1 : 1);
		taskQuery.setConversationRecords(conversationRecords);
		AligenieSessionUtil.updateTaskQuery(taskQuery);
		LOGGER.info("session info:" + GSON.toJson(taskQuery));
	}

	private void buildResult(TaskQuery taskQuery, TaskResult result, String any, String sequence, String back) {

		if (StringUtils.isEmpty(any) && StringUtils.isEmpty(sequence) && StringUtils.isEmpty(back)) {
			result.setReply("请告诉我你要干嘛，比如 天猫精灵 智能问答 请问如何填写时间成本");
			result.setResultType(ResultType.ASK_INF);
		} else if (isSecond(sequence)) {
			int index = NumberUtil.chineseNumber2Int(sequence);
			LOGGER.info("index is:" + index);
			result.setReply(getDetailAnswer(taskQuery, index + "", index));
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
				List<ArticleDTO> articleDTOs = getArticles(any, words);
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
		if(getDialogFromSession(taskQuery).size() > 1){
			LOGGER.info("用户上一次输入为:" + getDialogFromSession(taskQuery).get(0).getUserInputUtterance());
			if (!getDialogFromSession(taskQuery).get(0).getUserInputUtterance().equals("返回上一条")) {
				reply = getDialogFromSession(taskQuery).get(1).getReplyUtterance() + "_";
                LOGGER.info("there have no return back before");
                articleService.getAndSendArticlesFromContext(reply);
			} else {
				String record = getDialogFromSession(taskQuery).get(0).getReplyUtterance().replaceAll("_", "");
				LOGGER.info("上一条天猫精灵输出为:" + record );
				int index = -1;
				for (int i = 0; i < getDialogFromSession(taskQuery).size(); i++) {
					if (record.equals(getDialogFromSession(taskQuery).get(i).getReplyUtterance())) {
						index = i;
						break;
					}
				}
				LOGGER.info("后退索引为：" + (index + 1) + "size:" + getDialogFromSession(taskQuery).size());
				if(index != -1 && index + 1 <  getDialogFromSession(taskQuery).size()){
					reply =getDialogFromSession(taskQuery).get(index + 1).getReplyUtterance() + "_";
                    articleService.getAndSendArticlesFromContext(reply);
				}else{
					LOGGER.info("历史记录里未找到：" + record);
				}
				
			}
		}else{
			LOGGER.info("不能返回上一条因为记录大小为:" + getDialogFromSession(taskQuery).size());
		}
		return reply;
	}

	private boolean isBack(String back) {
		return StringUtils.isNotBlank(back);
	}

	private void buildReply(TaskResult result,String any, List<ArticleDTO> articleDTOs) {
		if (CollectionUtils.isEmpty(articleDTOs)) {
			LOGGER.info("分词结果未在数据库查询到相关tag");
			result.setReply(articleService.buildReplyResult(articleDTOs));
			result.setResultType(ResultType.ASK_INF);
		} else if (articleDTOs.size() == 1) {
            LOGGER.info("查询到的文章，title：" + articleDTOs.get(0).getTitle() + "  url:"+articleDTOs.get(0).getUrl());
            articleService.recordAndSendArticles(any,articleDTOs);
			result.setReply(articleService.buildReplyResult(articleDTOs));
			result.setResultType(ResultType.ASK_INF);
		} else {
            articleService.recordAndSendArticles(any,articleDTOs);
			result.setReply(articleService.buildReplyResult(articleDTOs));
			result.setResultType(ResultType.ASK_INF);
		}
	}

	private List<ArticleDTO> getArticles(String any, List<String> words) {
		// help to get articles
		List<ArticleDTO> res = articleService.getArticleByWords(any, words);
		return res;
	}

	private String getDetailAnswer(TaskQuery taskQuery, String any, int index) {

		ConversationRecord conversationRecord = getDialogFromSession(taskQuery).stream().findFirst().orElse(null);
		if (null != conversationRecord) {
			List<String> answers = Arrays.asList(conversationRecord.getReplyUtterance().split(","));
			if (index > answers.size()) {
				return "请勿捣蛋";
			}
			// TODO 
			// help to redirect url by socket(content page)
            String title = answers.get(index - 1);
            LOGGER.info("get detail answer by title:" + title);
            ArticleDTO articleDetail = articleService.getArticleDetailFromContext(title);
            LOGGER.info("the detail answer is:" + articleDetail == null ? "" : articleDetail.getContent());

			if (articleDetail != null){
                articleService.recordAndSendArticles(any,Arrays.asList(articleDetail));
			    return articleDetail.getContent();
            }
            else{
                LOGGER.info("can not get detail answer by title:" + title);
            }

		}
		return "抱歉，未找到上下文，请重新查询";
	}

	private List<ConversationRecord> getDialogFromSession(TaskQuery taskQuery) {
		return AligenieSessionUtil.getTaskQuery(taskQuery).getConversationRecords();
	}

	private boolean isSecond(String sequence) {
		return StringUtils.isNotEmpty(sequence);
	}
	
}
