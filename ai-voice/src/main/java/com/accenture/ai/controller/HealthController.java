package com.accenture.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	
	private static final String HEALTH_MESSAGE = "OK";

	@GetMapping("/_health")
	public String health() {
		return HEALTH_MESSAGE;
	}
	
	/*@GetMapping("/")
	public String hello() {
		return HEALTH_MESSAGE;
	}*/
}

