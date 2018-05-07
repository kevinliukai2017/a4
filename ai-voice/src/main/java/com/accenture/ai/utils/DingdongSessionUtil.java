package com.accenture.ai.utils;

import java.util.HashMap;
import java.util.Map;

import com.accenture.ai.model.dingdong.TaskQuery;


public class DingdongSessionUtil {

	private DingdongSessionUtil() {
	}

	private static class AligenieSessionFactory {
		private static Map<String, TaskQuery> instance = new HashMap<>();
	}

	public static Map<String, TaskQuery> getInstance() {
		return AligenieSessionFactory.instance;
	}

	public static void addTaskQuery(TaskQuery taskQuery) {
		Map<String, TaskQuery> aligenieSession = DingdongSessionUtil.getInstance();
		if (null == aligenieSession.get(taskQuery.getSession().getSessionId())) {
			aligenieSession.put(taskQuery.getSession().getSessionId(), taskQuery);
		}
	}

	public static void updateTaskQuery(TaskQuery taskQuery) {
		Map<String, TaskQuery> aligenieSession = DingdongSessionUtil.getInstance();
		aligenieSession.put(taskQuery.getSession().getSessionId(), taskQuery);
	}

	public static TaskQuery getTaskQuery(TaskQuery taskQuery) {
		Map<String, TaskQuery> aligenieSession = DingdongSessionUtil.getInstance();
		return aligenieSession.get(taskQuery.getSession().getSessionId());
	}
}
