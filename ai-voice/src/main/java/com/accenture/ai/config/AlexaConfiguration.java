package com.accenture.ai.config;

import com.accenture.ai.controller.alexa.HelloSpeechlet;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
public class AlexaConfiguration {
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
}
