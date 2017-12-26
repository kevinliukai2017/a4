package com.accenture.ai;

import com.accenture.ai.alexa.HelloSpeechlet;
import com.amazon.speech.Sdk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazon.speech.speechlet.servlet.SpeechletServlet;

@Controller
@SpringBootApplication
@EnableAutoConfiguration
public class AIVOICEApplication {

	@Bean
	public HelloSpeechlet helloSpeechlet(){

		return new HelloSpeechlet();
	}

	@Bean
	public SpeechletServlet speechletServlet(){
		SpeechletServlet ret = new SpeechletServlet();
		ret.setSpeechlet(helloSpeechlet());
		return ret;
	}

	@Bean
	public ServletRegistrationBean alexaConfig() throws Exception {
		ServletRegistrationBean ret = new ServletRegistrationBean(speechletServlet(),"/hialex");
		return ret;
	}

	@RequestMapping("/alexa")
	@ResponseBody
	String home() {
		return "Hello World!";
	}

	private static void setAmazonProperties() {
		// Disable signature checks for development
		System.setProperty(Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY, "true");
		// Allow all application ids for development
		System.setProperty(Sdk.SUPPORTED_APPLICATION_IDS_SYSTEM_PROPERTY, "");
		// Disable timestamp verification for development
		System.setProperty(Sdk.TIMESTAMP_TOLERANCE_SYSTEM_PROPERTY, "");
	}

	public static void main(String[] args)throws Exception {
		setAmazonProperties();
		SpringApplication.run(AIVOICEApplication.class, args);
	}
}
