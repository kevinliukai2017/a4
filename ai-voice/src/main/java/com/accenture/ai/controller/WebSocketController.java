package com.accenture.ai.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.accenture.ai.utils.ArticleResultContex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.accenture.ai.utils.SocketMessage;
import com.accenture.ai.utils.SocketStatusContex;

@Controller
@EnableScheduling
public class WebSocketController {
	
	@Autowired  
	SocketStatusContex socketStatusContex;

    @Autowired
    ArticleResultContex articleResultContex;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/websocket/home")
    public String home() {
        return "home";
    }
    
    @GetMapping("/websocket/defaultFrame")
    public String defaultFrame() {
        return "defaultFrame";
    }
    
    @GetMapping("/websocket/salesAmountFrame")
    public String marketingFrame() {
        return "salesAmountFrame";
    }
    
    @GetMapping("/websocket/monthSalesFrame")
    public String monthSalesFrame() {
        return "monthSalesFrame";
    }
    
    @GetMapping("/websocket/quarterSalesFrame")
    public String quarterSalesFrame() {
        return "quarterSalesFrame";
    }
    
    @GetMapping("/websocket/yearSalesFrame")
    public String monitorFrame() {
        return "yearSalesFrame";
    }
    
    @GetMapping("/websocket/technologyFrame")
    public String technologyFrame() {
        return "technologyFrame";
    }
    
    @GetMapping("/websocket/customerSalesFrame")
    public String customerSalesFrame() {
        return "customerSalesFrame";
    }
    
    @GetMapping("/websocket/productSalesFrame")
    public String productSalesFrame() {
        return "productSalesFrame";
    }
    
    @GetMapping("/websocket/storeSalesFrame")
    public String storeSalesFrame() {
        return "storeSalesFrame";
    }

    @GetMapping("/websocket/articleListFrame")
    public String articleListFrame(Map<String,Object> model) {
        model.put("articles",articleResultContex.getArticles());
        return "articleListFrame";
    }

    //该方法是客户端请求服务器时，服务器的处理请求的方法
    @MessageMapping("/send")
    @SendTo("/topic/send")
    public SocketMessage send(SocketMessage message) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.setDate(df.format(new Date()));
        return message;
    }

    //该方法是服务器定时发信息给客户端
    //@Scheduled(fixedRate = 1000)
    @SendTo("/topic/callback")
    public Object callback() throws Exception {
        // 发现消息
        messagingTemplate.convertAndSend("/topic/callback", socketStatusContex);
        return "callback";
    }
}
