package com.accenture.ai.controller.dingdong;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
import com.accenture.ai.utils.IKAnalyzerUtil;

@Service
public class SmartQAServiceImpl{

	private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartQAServiceImpl.class);

	@Autowired
	ArticleService articleService;
    

	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("SmartQAServiceImpl start -----------");
		TaskResult result = new TaskResult();
		String inputText = taskQuery.getInputText();
		buildResult(inputText, result,taskQuery);
		LOGGER.info("SmartQAServiceImpl end --------------");
		return result;
	}


	private void buildResult(String inputText, TaskResult result, TaskQuery taskQuery) {

		result.setVersionid(taskQuery.getVersionid());
		result.setIsEnd(false);
		result.setSequence(taskQuery.getSequence());
		result.setTimestamp(System.currentTimeMillis());
		
		if (StringUtils.isEmpty(inputText) ) {
			buildReplyInfo(result,"不知道你要干什么");
		} else {
			try {
				String splitWords = IKAnalyzerUtil.wordSplit(inputText);
				LOGGER.info("分词结果:" + splitWords);
				List<String> words = Arrays.asList(splitWords.split(","));
				List<ArticleDTO> articleDTOs = getArticles(inputText, words);
				buildReply(result,inputText, articleDTOs);
			} catch (IOException e) {
				buildReplyInfo(result,"分词发生异常,请联系管理员");
				LOGGER.error("Exception happend when spliting word" + e.getMessage());
			}
		}
	}

	private void buildReplyInfo(TaskResult result, String replyInfo) {
		Directive directive = new Directive();
		DirectiveItem directiveItem = new DirectiveItem();
		directiveItem.setType("TTS");
		directiveItem.setContent(replyInfo);
		DirectiveItem[] directiveItems = {directiveItem};
		directive.setDirectiveItems(directiveItems);
		result.setDirective(directive);
	}



	private void buildReply(TaskResult result,String any, List<ArticleDTO> articleDTOs) {
		if (CollectionUtils.isEmpty(articleDTOs)) {
			LOGGER.info("分词结果未在数据库查询到相关tag");
		} else if (articleDTOs.size() == 1) {
            LOGGER.info("查询到的文章，title：" + articleDTOs.get(0).getTitle() + "  url:"+articleDTOs.get(0).getUrl());
            articleService.recordAndSendArticles(any,articleDTOs);
		} else {
            articleService.recordAndSendArticles(any,articleDTOs);
		}
		buildReplyInfo(result,articleService.buildReplyResult(articleDTOs));
	}

	private List<ArticleDTO> getArticles(String any, List<String> words) {
		// help to get articles
		List<ArticleDTO> res = articleService.getArticleByWords(any, words);
		return res;
	}



	
}
