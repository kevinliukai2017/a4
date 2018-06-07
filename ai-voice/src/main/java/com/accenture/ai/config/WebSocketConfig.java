package com.accenture.ai.config;

import com.accenture.ai.utils.ArticleResultContex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.accenture.ai.utils.SocketStatusContex;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	private SocketStatusContex socketStatusContex;
	private ArticleResultContex articleResultContex;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/websocket");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/my-websocket").withSockJS();
    }
    
    @Bean
    public SocketStatusContex socketStatusContex(){
    		if(socketStatusContex == null) {
    			socketStatusContex = new SocketStatusContex();
    			socketStatusContex.setTitle("欢迎光临门户信息系统");
    			//socketStatusContex.setUrl("/websocket/defaultFrame");
                socketStatusContex.setUrl("/websocket/categoryListFrame");
    		}
    		
		return socketStatusContex;
	}

    @Bean
    public ArticleResultContex articleResultContex(){
        if(articleResultContex == null) {
            articleResultContex = new ArticleResultContex();
        }

        return articleResultContex;
    }

}
