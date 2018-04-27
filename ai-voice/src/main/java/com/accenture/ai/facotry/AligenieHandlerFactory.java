package com.accenture.ai.facotry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.accenture.ai.service.aligenie.v2.AligenieHandler;
import com.accenture.ai.service.aligenie.v2.impl.SmartDevicePOCHandler;

@Configuration
public class AligenieHandlerFactory {
	
	@Bean
	public AligenieHandler smartDevicePOCHandler(){
		return new SmartDevicePOCHandler();
	}
}
