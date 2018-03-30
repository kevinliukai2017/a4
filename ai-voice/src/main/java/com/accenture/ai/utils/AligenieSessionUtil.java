package com.accenture.ai.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.da.coin.ide.spi.standard.TaskQuery;

public class AligenieSessionUtil {

	public static Map<String, TaskQuery> aligenieSession = new HashMap<>();
	
	public static void addTaskQuery(TaskQuery taskQuery){
		if(null == aligenieSession.get(taskQuery.getSessionId())){
			aligenieSession.put(taskQuery.getSessionId(), taskQuery);
		}
	}
	
	public static void updateTaskQuery(TaskQuery taskQuery){
			aligenieSession.put(taskQuery.getSessionId(), taskQuery);
	}
	
	public static TaskQuery getTaskQuery(TaskQuery taskQuery){
		return aligenieSession.get(taskQuery.getSessionId());
}
}
