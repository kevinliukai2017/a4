package com.accenture.ai.service.aligenie;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		buildResult(taskQuery, result, any);
		LOGGER.info("SmartDevicePOCServiceImpl end --------------");
		return result;
	}

	private void buildResult(TaskQuery taskQuery, TaskResult result, String any) {

		if (StringUtils.isEmpty(any)) {
			result.setReply("请告诉我你要干嘛，比如 天猫精灵 智能问答 如何填写时间成本");
			result.setResultType(ResultType.RESULT);
		} else if (isSecond(any)) {
			int index = NumberUtil.chineseNumber2Int(any);

			result.setReply(getDetailAnswer(taskQuery, index));
			result.setResultType(ResultType.RESULT);
		} else {
			try {
				String splitWords = IKAnalyzerUtil.wordSplit(any);
				LOGGER.info("分词结果:" + splitWords);
				List<String> words = Arrays.asList(splitWords.split(","));
				
				result.setResultType(ResultType.ASK_INF);
			} catch (IOException e) {
				result.setReply("分词发生异常,请联系管理员");
				result.setResultType(ResultType.RESULT);
				LOGGER.error("Exception happend when spliting word" + e.getMessage());
			}
		}
	}

	private String getDetailAnswer(TaskQuery taskQuery, int index) {

		ConversationRecord conversationRecord = taskQuery.getConversationRecords().stream().findFirst().orElse(null);
		if (null != conversationRecord) {
			List<String> answers = Arrays.asList(conversationRecord.getReplyUtterance().split(","));
			return answers.get(index - 1);
		}
		return "抱歉，未找到上下文，请重新查询";
	}

	private boolean isSecond(String any) {
		return any.startsWith("第");
	}
}
