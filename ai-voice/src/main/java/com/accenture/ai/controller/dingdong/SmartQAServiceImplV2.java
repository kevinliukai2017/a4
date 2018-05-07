package com.accenture.ai.controller.dingdong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

	@Autowired
	ArticleService articleService;
    
    private static final Gson GSON = new Gson();

	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("SmartDevicePOCServiceImpl start -----------");
		Map<String, String> paramMap = taskQuery.getSlots();
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String any = paramMap.get("any");
		String sequence = paramMap.get("sequence");
		String back = paramMap.get("back");
		buildResult(taskQuery, result, any,sequence,back);
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
		
		if (StringUtils.isEmpty(any) && StringUtils.isEmpty(sequence) && StringUtils.isEmpty(back)) {
			
			buildDingdongInfo(result,"请告诉我你要干嘛，比如 天猫精灵 智能问答 请问如何填写时间成本");
			
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
            LOGGER.info("the detail answer is:" + articleDetail == null ? "" : articleDetail.getContent());

			if (articleDetail != null){
                articleService.recordAndSendArticles(any,Arrays.asList(articleDetail));
			    return StringUtils.isNotEmpty(articleDetail.getExcerpt()) ? articleDetail.getExcerpt() : articleDetail.getContent();
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
		directiveItem.setType("TTS");
		directiveItem.setContent(replyInfo);
		DirectiveItem[] directiveItems = {directiveItem};
		directive.setDirectiveItems(directiveItems);
		result.setDirective(directive);
	}
	
}
