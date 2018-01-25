package com.accenture.ai.service.aligenie;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.accenture.ai.logging.LogAgent;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

@Service
public class POCServiceImpl {
	
	private static final LogAgent LOGGER = LogAgent.getLogAgent(POCServiceImpl.class);
	
	public TaskResult pocHandle(TaskQuery taskQuery) {
		LOGGER.info("pocHandle start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
				.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String count = paramMap.get("count");
		String product = paramMap.get("product");
		String replyValue = "";
		if("洗衣粉".equals(product)){
			replyValue = "您是想购买蓝月亮洗衣粉还是好爸爸洗衣粉";
		}
		
		if("蓝月亮洗衣粉".equals(product) || "好爸爸洗衣粉".equals(product)){
			replyValue = count + " " +  product + " 购买成功";
		}
		result.setReply(replyValue);
		result.setResultType(ResultType.RESULT);
		LOGGER.info("pocHandle end --------------");
		return result;
	}
	
}
