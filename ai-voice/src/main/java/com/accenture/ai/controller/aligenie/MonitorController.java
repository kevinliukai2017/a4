package com.accenture.ai.controller.aligenie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.ai.constant.AIConstants;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.aligenie.MointorSalesSummaryPOCServiceImpl;
import com.accenture.ai.service.aligenie.OrderCountPOCServiceImpl;
import com.alibaba.da.coin.ide.spi.standard.ResultModel;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;
import com.alibaba.da.coin.ide.spi.trans.MetaFormat;

@RestController
@RequestMapping(path = "/aligenie/v1/monitor")
public class MonitorController {
	
	/**
	 * logger Factory
	 */

	private static final LogAgent LOGGER = LogAgent.getLogAgent(MonitorController.class);
	
	@Autowired
	private MointorSalesSummaryPOCServiceImpl salesSummaryPocService;
	
	@Autowired
	private OrderCountPOCServiceImpl orderCountPOCService;
	
	@RequestMapping(path = "/salesSummary", method = { RequestMethod.POST })
	@ResponseBody
	public ResultModel<TaskResult> salesSummary(@RequestBody String taskQuery) {
		// parse json to taskquery
		LOGGER.info("TaskQuery:{}", taskQuery.toString());
		TaskQuery query = MetaFormat.parseToQuery(taskQuery);
		// prepare result
		ResultModel<TaskResult> resultModel = new ResultModel<TaskResult>();

		try {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_SUCCESS);
			resultModel.setReturnValue(salesSummaryPocService.handle(query));
		} catch (Exception e) {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_FAIL);
			resultModel.setReturnErrorSolution(e.getMessage());
		}

		return resultModel;
	}
	
	@RequestMapping(path = "/orderCount", method = { RequestMethod.POST })
	@ResponseBody
	public ResultModel<TaskResult> orderCount(@RequestBody String taskQuery) {
		// parse json to taskquery
		LOGGER.info("TaskQuery:{}", taskQuery.toString());
		TaskQuery query = MetaFormat.parseToQuery(taskQuery);
		// prepare result
		ResultModel<TaskResult> resultModel = new ResultModel<TaskResult>();

		try {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_SUCCESS);
			resultModel.setReturnValue(orderCountPOCService.handle(query));
		} catch (Exception e) {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_FAIL);
			resultModel.setReturnErrorSolution(e.getMessage());
		}

		return resultModel;
	}
}
