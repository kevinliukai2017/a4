package com.accenture.ai.service.aligenie;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.utils.SocketStatusContex;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

@Service
public class SalesSummaryPOCServiceImpl extends AbstractAligenieService{
	
	private static final LogAgent LOGGER = LogAgent.getLogAgent(POCServiceImpl.class);
	
	@Autowired  
	SocketStatusContex socketStatusContex;
	
	@Override
	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("salesSummaryPOC start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
				.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String period = paramMap.get("period");
		String replyValue = "";
		if("年度销量".equals(period)){
			socketStatusContex.setTitleAndUrl("年度销量", "/websocket/yearSalesFrame");
			replyValue = "好的，现在的年度总销量为十二亿，距离年度目标还差一亿";
		} else if("月度销量".equals(period)){
			socketStatusContex.setTitleAndUrl("月度销量", "/websocket/monthSalesFrame");
			replyValue = "好的，现在的月度总销量为七千万，距离年度目标还差两百万";
		} else if ("季度销量".equals(period)){
			socketStatusContex.setTitleAndUrl("季度销量", "/websocket/quarterSalesFrame");
			replyValue = "好的，现在的季度总销量为三亿，距离年度目标还差一千万";
		} else {
			socketStatusContex.setTitleAndUrl("月度销量", "/websocket/monthSalesFrame");
		}
		result.setReply(replyValue);
		result.setResultType(ResultType.RESULT);
		LOGGER.info("salesSummaryPOC end --------------");
		return result;
	}
}
