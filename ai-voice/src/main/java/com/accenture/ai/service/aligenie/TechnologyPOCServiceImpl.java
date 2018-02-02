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
public class TechnologyPOCServiceImpl extends AbstractAligenieService{
	
	private static final LogAgent LOGGER = LogAgent.getLogAgent(TechnologyPOCServiceImpl.class);
	
	@Autowired  
	SocketStatusContex socketStatusContex;
	
	@Override
	public TaskResult handle(TaskQuery taskQuery) {
		LOGGER.info("technologyPOC start -----------");
		Map<String, String> paramMap = taskQuery.getSlotEntities().stream().collect(Collectors
				.toMap(slotItem -> slotItem.getIntentParameterName(), slotItem -> slotItem.getStandardValue()));
		LOGGER.info("paramMap ：" + paramMap.toString());
		// test content
		TaskResult result = new TaskResult();
		String period = paramMap.get("part");
		String replyValue = "";
		if("硬盘".equals(period)){
			replyValue = "硬盘总共五百G,剩余两百G可用";
		} else if("点击量".equals(period)){
			replyValue = "当日首页点击量为十一万次";
		} else if ("CPU".equals(period)){
			replyValue = "当前CPU使用率百分之七十三";
		} else if ("访问量".equals(period)){
			replyValue = "当前在线用户数为一千两百二十用户";
		} else if ("线程数".equals(period)){
			replyValue = "当前线程数为一千三百二十一";
		} else if ("内存".equals(period)){
			replyValue = "现在系统内存占用率为百分之二十一，情况稳定";
		}
		
		socketStatusContex.setTitleAndUrl("服务器信息", "/websocket/technologyFrame");
		
		result.setReply(replyValue);
		result.setResultType(ResultType.RESULT);
		LOGGER.info("technologyPOC end --------------");
		return result;
	}
}
