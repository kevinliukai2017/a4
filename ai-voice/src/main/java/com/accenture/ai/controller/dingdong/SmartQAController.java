package com.accenture.ai.controller.dingdong;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.ai.logging.LogAgent;

@RestController
@RequestMapping(path = "/dingdong/v1/smartqa")
public class SmartQAController {
	
	/**
	 * logger Factory
	 */

	private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartQAController.class);
	
	
	@RequestMapping(path = "/recordMeating", method = { RequestMethod.POST })
	@ResponseBody
	public String placeOrder(@RequestBody String taskQuery) {
		LOGGER.info("TaskQuery:{}", taskQuery.toString());

		return "OKPOST";
	}
	
	@RequestMapping(path = "/recordMeating", method = { RequestMethod.GET })
	@ResponseBody
	public String placeOrderTest(@RequestBody String taskQuery) {
		LOGGER.info("TaskQuery:{}", taskQuery.toString());

		return "OKGET";
	}
	
}
