package com.accenture.ai.service.aligenie;

import java.io.IOException;
import java.util.ArrayList;
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
import com.accenture.ai.utils.IKAnalyzerUtil;
import com.accenture.ai.utils.NumberUtil;
import com.accenture.ai.utils.SocketStatusContex;
import com.alibaba.da.coin.ide.spi.meta.ConversationRecord;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

@Service
public class SmartDevicePOCServiceImpl extends AbstractAligenieService {

	private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartDevicePOCServiceImpl.class);

	@Autowired
	SocketStatusContex socketStatusContex;

	@Override
	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("SmartDevicePOCServiceImpl start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
				.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String any = paramMap.get("any");
		String sequence = paramMap.get("sequence");
		buildResult(taskQuery, result, any,sequence);
		LOGGER.info("SmartDevicePOCServiceImpl end --------------");
		return result;
	}

	private void buildResult(TaskQuery taskQuery, TaskResult result, String any, String sequence) {

		if (StringUtils.isEmpty(any) && StringUtils.isEmpty(sequence)) {
			result.setReply("请告诉我你要干嘛，比如 天猫精灵 请问智能问答 如何填写时间成本");
			result.setResultType(ResultType.RESULT);
		} else if (isSecond(sequence)) {
			int index = NumberUtil.chineseNumber2Int(any);
			LOGGER.info("index is:" + index);
			result.setReply(getDetailAnswer(taskQuery, index));
			result.setResultType(ResultType.RESULT);
		} else {
			try {
				String splitWords = IKAnalyzerUtil.wordSplit(any);
				LOGGER.info("分词结果:" + splitWords);
				List<String> words = Arrays.asList(splitWords.split(","));
				List<ArticleDTO> articleDTOs = getArticles(words);
				buildReply(result, articleDTOs);
			} catch (IOException e) {
				result.setReply("分词发生异常,请联系管理员");
				result.setResultType(ResultType.RESULT);
				LOGGER.error("Exception happend when spliting word" + e.getMessage());
			}
		}
	}

	private void buildReply(TaskResult result, List<ArticleDTO> articleDTOs) {
		if (CollectionUtils.isEmpty(articleDTOs)) {
			LOGGER.info("分词结果未在数据库查询到相关tag");
			result.setReply("抱歉没有找到你想要的内容");
			result.setResultType(ResultType.RESULT);
		} else if (articleDTOs.size() == 1) {
			// TODO
			// help to redirect url by socket(content page)
			result.setReply(articleDTOs.get(0).getContent());
			result.setResultType(ResultType.RESULT);
		} else {
			String titles = "";
			for (ArticleDTO articleDTO : articleDTOs) {
				titles += articleDTO.getTitle() + ",";
			}
			// TODO 
			// help to redirect url by socket(list page)
			result.setReply(titles);
			result.setResultType(ResultType.ASK_INF);
		}
	}

	private List<ArticleDTO> getArticles(List<String> words) {
		// TODO 
		// help to get articles
		List<ArticleDTO> res = new ArrayList<ArticleDTO>();
		ArticleDTO a = new ArticleDTO();
		a.setTitle("公司机票怎么定");
		a.setContent("公司机票怎么定");
		ArticleDTO b = new ArticleDTO();
		b.setTitle("公司有没有快消行业实施案例");
		b.setContent("公司有没有快消行业实施案例");
		res.add(a);
		res.add(b);
		return res;
	}

	private String getDetailAnswer(TaskQuery taskQuery, int index) {

		ConversationRecord conversationRecord = taskQuery.getConversationRecords().stream().findFirst().orElse(null);
		if (null != conversationRecord) {
			List<String> answers = Arrays.asList(conversationRecord.getReplyUtterance().split(","));
			if (index > answers.size()) {
				return "请勿捣蛋";
			}
			// TODO 
			// help to redirect url by socket(content page)
			return answers.get(index - 1);
		}
		return "抱歉，未找到上下文，请重新查询";
	}

	private boolean isSecond(String sequence) {
		return StringUtils.isNotEmpty(sequence);
	}
}
