package com.accenture.ai;

import com.amazon.speech.Sdk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@EnableAutoConfiguration
public class AIVOICEApplication {
	public static void main(String[] args)throws Exception {
		SpringApplication.run(AIVOICEApplication.class, args);
	}
}
