package com.accenture.ai.controller.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.accenture.ai.service.aligenie.WebSocketServiceImpl;
import com.accenture.ai.utils.SocketStatusContex;


@Controller
@RequestMapping("/test")
public class TestController {
	
	@Autowired  
	SocketStatusContex socketStatusContex;
	
	@Autowired
	private WebSocketServiceImpl webSocketServiceImpl;
	
	@RequestMapping(value = "/changeStatus",method = RequestMethod.POST)
    @ResponseBody
    public String changeStatus(final String title, final String url) {
		socketStatusContex.setTitleAndUrl(title, url);
		
		//send contex to customer client
		webSocketServiceImpl.sendContexToClient();
		
		return "success";
    }

}
