package com.accenture.ai.controller.dingdong;

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.model.dingdong.Directive;
import com.accenture.ai.model.dingdong.DirectiveItem;
import com.accenture.ai.model.dingdong.TaskQuery;
import com.accenture.ai.model.dingdong.TaskResult;
import com.accenture.ai.service.article.ArticleService;
import com.accenture.ai.utils.DingdongSessionUtil;
import com.accenture.ai.utils.IKAnalyzerUtil;
import com.accenture.ai.utils.NumberUtil;
import com.alibaba.da.coin.ide.spi.meta.ConversationRecord;
import com.google.gson.Gson;

@Service
public class SmartQAServiceImplV2{

	private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartQAServiceImplV2.class);

	private static final String NO_PLEASE_REPLY_MESSAGE = "你是否有新的问题需要询问我？如果是的话请加上请问哦";
	private static final String NO_PLEASE_REPLY_END_MESSAGE = "您问的问题不包含请问，服务已断开，请重新唤醒小哲同学，谢谢";
	private static final String REQUEST_PARAMS_ERROR = "请求数据错误，请联系管理员，谢谢";
	private static final String END_SESSION_MESSAGE = "已退出，谢谢";
	private int noPleaseCount = 0;

	@Autowired
	ArticleService articleService;
    
    private static final Gson GSON = new Gson();

	public TaskResult handle(TaskQuery taskQuery) {
		TaskResult result = new TaskResult();
		LOGGER.info("SmartDevicePOCServiceImpl start -----------");

		if ("NOTICE".equals(taskQuery.getStatus())){
            LOGGER.info("The request status is NOTICE needn't to reply, will return empty content----------check the reqeust");
		    return result;
        }

        // if the status is end, we should close this session
        if ("END".equals(taskQuery.getStatus())){
			LOGGER.info("The request status is END, will end this session");
			buildspecialDingdongInfo(result,taskQuery,END_SESSION_MESSAGE,true);
			return result;
		}

		Map<String, String> paramMap = taskQuery.getSlots();

		if ("LAUNCH".equals(taskQuery.getStatus()) && MapUtils.isEmpty(paramMap)){
			LOGGER.info("The request status is LAUNCH, will end this session");
			buildspecialDingdongInfo(result,taskQuery,NO_PLEASE_REPLY_MESSAGE,true);
			return result;
		}

		if(MapUtils.isEmpty(paramMap)){
			LOGGER.info("The paramMap(slots) is null, will return empty content----------check the reqeust");
			buildspecialDingdongInfo(result,taskQuery,REQUEST_PARAMS_ERROR,false);
			return result;
		}
		LOGGER.info("paramMap ：" + paramMap.toString());

        String any = null;
		if ("other_any".equals(paramMap.get("type"))){
		    if (paramMap.get("value").startsWith("请问")){
				noPleaseCount =0;
                any = paramMap.get("value").replaceFirst("请问","");
            }else{
                LOGGER.info("The request is not first request and is not start with 请问, will return empty content----------check the reqeust");

				noPleaseCount++;

				if (noPleaseCount >= 3)
				{
					buildspecialDingdongInfo(result,taskQuery,NO_PLEASE_REPLY_END_MESSAGE,true);
				}
				else {
					buildspecialDingdongInfo(result,taskQuery,NO_PLEASE_REPLY_MESSAGE,false);
				}

                return result;
            }
        }
        else if ("first_any".equals(paramMap.get("type"))){
			noPleaseCount = 0;
            any = paramMap.get("value");
        }else if ("any".equals(paramMap.get("type"))){
			noPleaseCount = 0;
            if (paramMap.get("value").startsWith("请问")){
                any = paramMap.get("value").replaceFirst("请问","");
            }else{
                any = paramMap.get("value");
            }
        }else{
			noPleaseCount = 0;
		}
		
        String sequence = "sequence".equals(paramMap.get("type")) ? paramMap.get("value") : null  ;
        String back = "back".equals(paramMap.get("type")) ? paramMap.get("value") : null ;

        LOGGER.info("buildResult() any:" + any +", sequence:" + sequence + ", back:" + back);
        buildResult(taskQuery, result, any, sequence, back);
        buildDingdongSession(taskQuery,result);

		LOGGER.info("SmartDevicePOCServiceImpl end --------------");
		return result;
	}

	private void buildDingdongSession(TaskQuery taskQuery, TaskResult result) {
		String userInput = taskQuery.getInputText();
		String deviceOutput = result.getDirective().getDirectiveItems()[0].getContent();
		ConversationRecord record = new ConversationRecord();
		record.setUserInputUtterance(userInput);
		record.setReplyUtterance(deviceOutput);
		record.setTimestamp(System.currentTimeMillis());
		TaskQuery dailog =  DingdongSessionUtil.getTaskQuery(taskQuery);
		List<ConversationRecord> conversationRecords = dailog.getConversationRecords();
		if(CollectionUtils.isEmpty(conversationRecords)){
			conversationRecords = new ArrayList<ConversationRecord>();
		}
		conversationRecords.add(record);
		conversationRecords.sort((b1, b2) -> b1.getTimestamp() > b2.getTimestamp() ?  -1 : 1);
		taskQuery.setConversationRecords(conversationRecords);
		DingdongSessionUtil.updateTaskQuery(taskQuery);
		LOGGER.info("session info:" + GSON.toJson(taskQuery));
	}

	private void buildResult(TaskQuery taskQuery, TaskResult result, String any, String sequence, String back) {

		result.setVersionid(taskQuery.getVersionid());
		result.setIsEnd(false);
		result.setSequence(taskQuery.getSequence());
		result.setTimestamp(System.currentTimeMillis());
		Map<String, String> extend = new HashMap<>();
		extend.put("NO_REC", "0");
		result.setExtend(extend);

		if (StringUtils.isEmpty(any) && StringUtils.isEmpty(sequence) && StringUtils.isEmpty(back)) {
			
			buildDingdongInfo(result,"请告诉我你要干嘛，比如 请问如何填写时间成本");
			
		} else if (isSecond(sequence)) {
			int index = NumberUtil.chineseNumber2Int(sequence);
			LOGGER.info("index is:" + index);
			
			buildDingdongInfo(result,getDetailAnswer(taskQuery, index + "", index));
			
		} else if (isBack(back)) {
			String reply = buildBackReply(taskQuery);
			
			buildDingdongInfo(result,reply);
		} else {
			try {
				String splitWords = IKAnalyzerUtil.wordSplit(any);
				LOGGER.info("分词结果:" + splitWords);
				List<String> words = Arrays.asList(splitWords.split(","));
				List<ArticleDTO> articleDTOs = getArticles(any, words);
				buildReply(result,any, articleDTOs);
			} catch (IOException e) {
				buildDingdongInfo(result,"分词发生异常,请联系管理员");
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
			LOGGER.info("===========================");
			LOGGER.info(any);
			LOGGER.info("===========================");
			
			buildDingdongInfo(result,articleService.buildReplyResult(articleDTOs));
			
		} else if (articleDTOs.size() == 1) {
            LOGGER.info("查询到的文章，title：" + articleDTOs.get(0).getTitle() + "  url:"+articleDTOs.get(0).getUrl());
            articleService.recordAndSendArticles(any,articleDTOs);
            
            buildDingdongInfo(result,articleService.buildReplyResult(articleDTOs));
            
		} else {
            articleService.recordAndSendArticles(any,articleDTOs);
            buildDingdongInfo(result,articleService.buildReplyResult(articleDTOs));
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
            LOGGER.info("the detail answer is:" + (Objects.isNull(articleDetail) ? "" : articleDetail.getReadContent()));

			if (articleDetail != null){
                articleService.recordAndSendArticles(any,Arrays.asList(articleDetail));
			    return StringUtils.isNotEmpty(articleDetail.getReadExcerpt()) ? articleDetail.getReadExcerpt() :articleDetail.getReadContent();
            }
            else{
                LOGGER.info("can not get detail answer by title:" + title);
            }

		}
		return "抱歉，未找到上下文，请重新查询";
	}

	private List<ConversationRecord> getDialogFromSession(TaskQuery taskQuery) {
		return DingdongSessionUtil.getTaskQuery(taskQuery).getConversationRecords();
	}

	private boolean isSecond(String sequence) {
		return StringUtils.isNotEmpty(sequence);
	}
	
	private void buildDingdongInfo(TaskResult result, String replyInfo) {
		Directive directive = new Directive();
		DirectiveItem directiveItem = new DirectiveItem();
		directiveItem.setType("1");
		directiveItem.setContent(replyInfo);
		DirectiveItem[] directiveItems = {directiveItem};
		directive.setDirectiveItems(directiveItems);
		result.setDirective(directive);
	}

	private void buildspecialDingdongInfo(TaskResult result,TaskQuery taskQuery,String replyMessage,boolean isEnd){
        result.setVersionid(taskQuery.getVersionid());
        result.setIsEnd(isEnd);
        result.setSequence(taskQuery.getSequence());
        result.setTimestamp(System.currentTimeMillis());
        Map<String, String> extend = new HashMap<>();
        extend.put("NO_REC", "0");
        result.setExtend(extend);

        Directive directive = new Directive();
        DirectiveItem directiveItem = new DirectiveItem();
        directiveItem.setType("1");
        directiveItem.setContent(replyMessage);
        DirectiveItem[] directiveItems = {directiveItem};
        directive.setDirectiveItems(directiveItems);
        result.setDirective(directive);
    }
	
}
