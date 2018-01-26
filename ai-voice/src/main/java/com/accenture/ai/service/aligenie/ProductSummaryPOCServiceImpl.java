package com.accenture.ai.service.aligenie;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.accenture.ai.logging.LogAgent;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

@Service
public class ProductSummaryPOCServiceImpl extends AbstractAligenieService{
	
	private static final LogAgent LOGGER = LogAgent.getLogAgent(POCServiceImpl.class);
	
	@Override
	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("productSummaryPOC start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
				.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String period = paramMap.get("period");
		String replyValue = "";
		if("年度销量".equals(period)){
			replyValue = "好的，年度销量最好的产品为好爸爸洗衣液";
		} else if("月度销量".equals(period)){
			replyValue = "好的，月度销量最好的产品为蓝月亮洗衣液";
		} else if ("季度销量".equals(period)){
			replyValue = "好的，季度销量最好的产品为立白洗洁精";
		}
		result.setReply(replyValue);
		result.setResultType(ResultType.RESULT);
		LOGGER.info("productSummaryPOC end --------------");
		return result;
	}
}
