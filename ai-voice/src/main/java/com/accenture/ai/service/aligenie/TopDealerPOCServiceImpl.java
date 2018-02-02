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
public class TopDealerPOCServiceImpl extends AbstractAligenieService{
	
	private static final LogAgent LOGGER = LogAgent.getLogAgent(TopDealerPOCServiceImpl.class);
	
	@Autowired  
	SocketStatusContex socketStatusContex;
	
	@Override
	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("TopDealerPOC start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
				.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		
		socketStatusContex.setTitleAndUrl("经销商销量信息", "/websocket/customerSalesFrame");
		
		String replyValue = "销量前三的经销商为上海经销商,大连经销商,北京经销商";
		result.setReply(replyValue);
		result.setResultType(ResultType.RESULT);
		LOGGER.info("TopDealerPOC end --------------");
		return result;
	}
}
