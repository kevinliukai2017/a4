package com.accenture.ai.controller.dingdong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.model.dingdong.TaskQuery;
import com.accenture.ai.model.dingdong.TaskResult;
import com.accenture.ai.utils.AligenieSessionUtil;
import com.accenture.ai.utils.DingdongSessionUtil;
import com.accenture.ai.utils.MetaFormat;

@RestController
@RequestMapping(path = "/dingdong/v1/smartqa")
public class SmartQAController {

	/**
	 * logger Factory
	 */
	@Autowired
	private SmartQAServiceImplV2 smartQAServiceImpl;

	private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartQAController.class);

	@RequestMapping(path = "/answer", method = { RequestMethod.POST })
	@ResponseBody
	public TaskResult placeOrder(@RequestBody String taskQuery) {
		LOGGER.info("TaskQuery:{}", taskQuery.toString());
		TaskQuery query = MetaFormat.parseToQuery(taskQuery);
		// prepare result
		DingdongSessionUtil.addTaskQuery(query);
		return smartQAServiceImpl.handle(query);

	}

}
