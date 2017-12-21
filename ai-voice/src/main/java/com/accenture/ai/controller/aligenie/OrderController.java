package com.accenture.ai.controller.aligenie;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.ai.logging.LogAgent;
import com.alibaba.da.coin.ide.spi.meta.ResultType;
import com.alibaba.da.coin.ide.spi.standard.ResultModel;
import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;
import com.alibaba.da.coin.ide.spi.trans.MetaFormat;

@RestController
@RequestMapping(path = "/aligenie/v1/order")
public class OrderController {
	
	/**
	 * logger Factory
	 */

	private static final LogAgent LOGGER = LogAgent.getLogAgent(OrderController.class);
	
	@RequestMapping(path = "/placeorder", method = { RequestMethod.POST })
	@ResponseBody
	public ResultModel<TaskResult> getResponse(@RequestBody String taskQuery) {
		// parse json to taskquery
		LOGGER.info("TaskQuery:{}", taskQuery.toString());
		TaskQuery query = MetaFormat.parseToQuery(taskQuery);

		// prepare result
		ResultModel<TaskResult> resultModel = new ResultModel<TaskResult>();

		try {
			// test content
			TaskResult result = new TaskResult();
			String count = query.getRequestData().get("count");
			String product = query.getRequestData().get("product");
			result.setReply(count + product + "购买成功");
			result.setResultType(ResultType.RESULT);
			resultModel.setReturnCode("0");
			resultModel.setReturnValue(result);
		} catch (Exception e) {
			resultModel.setReturnCode("-1");
			resultModel.setReturnErrorSolution(e.getMessage());
		}

		return resultModel;
	}
}
