package com.accenture.ai.controller.aligenie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.ai.constant.AIConstants;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.aligenie.OrderCountPOCServiceImpl;
import com.accenture.ai.service.aligenie.ProductSummaryPOCServiceImpl;
import com.accenture.ai.service.aligenie.SalesSummaryPOCServiceImpl;
import com.accenture.ai.service.aligenie.TechnologyPOCServiceImpl;
import com.accenture.ai.service.aligenie.TopDealerPOCServiceImpl;
import com.accenture.ai.service.aligenie.WebSocketServiceImpl;
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
	private SalesSummaryPOCServiceImpl salesSummaryPocService;
	
	@Autowired
	private OrderCountPOCServiceImpl orderCountPOCService;
	
	@Autowired
	private TechnologyPOCServiceImpl technologyPOCServiceImpl;
	
	@Autowired
	private TopDealerPOCServiceImpl topDealerPOCServiceImpl;
	
	@Autowired
	private ProductSummaryPOCServiceImpl productSummaryPOCServiceImpl;
	
	@Autowired
	private WebSocketServiceImpl webSocketServiceImpl;
	
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
		
		//send contex to customer client
		webSocketServiceImpl.sendContexToClient();

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
		
		//send contex to customer client
		//webSocketServiceImpl.sendContexToClient();
		
		return resultModel;
	}
	
	@RequestMapping(path = "/technology", method = { RequestMethod.POST })
	@ResponseBody
	public ResultModel<TaskResult> technology(@RequestBody String taskQuery) {
		// parse json to taskquery
		LOGGER.info("TaskQuery:{}", taskQuery.toString());
		TaskQuery query = MetaFormat.parseToQuery(taskQuery);
		// prepare result
		ResultModel<TaskResult> resultModel = new ResultModel<TaskResult>();

		try {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_SUCCESS);
			resultModel.setReturnValue(technologyPOCServiceImpl.handle(query));
		} catch (Exception e) {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_FAIL);
			resultModel.setReturnErrorSolution(e.getMessage());
		}
		
		//send contex to customer client
		webSocketServiceImpl.sendContexToClient();

		return resultModel;
	}
	
	@RequestMapping(path = "/topDealers", method = { RequestMethod.POST })
	@ResponseBody
	public ResultModel<TaskResult> topDealers(@RequestBody String taskQuery) {
		// parse json to taskquery
		LOGGER.info("TaskQuery:{}", taskQuery.toString());
		TaskQuery query = MetaFormat.parseToQuery(taskQuery);
		// prepare result
		ResultModel<TaskResult> resultModel = new ResultModel<TaskResult>();

		try {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_SUCCESS);
			resultModel.setReturnValue(topDealerPOCServiceImpl.handle(query));
		} catch (Exception e) {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_FAIL);
			resultModel.setReturnErrorSolution(e.getMessage());
		}
		
		//send contex to customer client
		webSocketServiceImpl.sendContexToClient();

		return resultModel;
	}
	
	@RequestMapping(path = "/productSummary", method = { RequestMethod.POST })
	@ResponseBody
	public ResultModel<TaskResult> productSummary(@RequestBody String taskQuery) {
		// parse json to taskquery
		LOGGER.info("TaskQuery:{}", taskQuery.toString());
		TaskQuery query = MetaFormat.parseToQuery(taskQuery);
		// prepare result
		ResultModel<TaskResult> resultModel = new ResultModel<TaskResult>();

		try {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_SUCCESS);
			resultModel.setReturnValue(productSummaryPOCServiceImpl.handle(query));
		} catch (Exception e) {
			resultModel.setReturnCode(AIConstants.ALIGENIE_RETURN_FAIL);
			resultModel.setReturnErrorSolution(e.getMessage());
		}
		
		//send contex to customer client
		webSocketServiceImpl.sendContexToClient();

		return resultModel;
	}
}
