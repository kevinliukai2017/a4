package com.accenture.ai.service.aligenie;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.order.OrderServiceImpl;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

@Service
public class OrderCountPOCServiceImpl extends AbstractAligenieService{
	
	private static final LogAgent LOGGER = LogAgent.getLogAgent(OrderCountPOCServiceImpl.class);
	
	@Autowired
	private OrderServiceImpl orderService;
	
	@Override
	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("orderCountPOC start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
				.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String orderStatus = paramMap.get("status");
		String month = paramMap.get("month");
		String replyValue = orderService.getOrderCount(month, orderStatus);
		result.setReply(replyValue);
		if("正常".equals(orderStatus)){
			result.setResultType(ResultType.ASK_INF);
		}else{
			result.setResultType(ResultType.RESULT);
		}
		LOGGER.info("orderCountPOC end --------------");
		return result;
	}
}
