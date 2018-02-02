package com.accenture.ai.service.aligenie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.accenture.ai.utils.SocketStatusContex;

@Service
public class WebSocketServiceImpl {
	
	@Autowired  
	SocketStatusContex socketStatusContex;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	@SendTo("/topic/callback")
	public Object sendContexToClient() {
		messagingTemplate.convertAndSend("/topic/callback", socketStatusContex);
        return "callback";
	}

}
