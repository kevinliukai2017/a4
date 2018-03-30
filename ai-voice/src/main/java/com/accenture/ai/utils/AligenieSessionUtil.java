package com.accenture.ai.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.da.coin.ide.spi.standard.TaskQuery;

public class AligenieSessionUtil {

	private AligenieSessionUtil() {
	}

	private static class AligenieSessionFactory {
		private static Map<String, TaskQuery> instance = new HashMap<>();
	}

	public static Map<String, TaskQuery> getInstance() {
		return AligenieSessionFactory.instance;
	}

	public static void addTaskQuery(TaskQuery taskQuery) {
		Map<String, TaskQuery> aligenieSession = AligenieSessionUtil.getInstance();
		if (null == aligenieSession.get(taskQuery.getSessionId())) {
			aligenieSession.put(taskQuery.getSessionId(), taskQuery);
		}
	}

	public static void updateTaskQuery(TaskQuery taskQuery) {
		Map<String, TaskQuery> aligenieSession = AligenieSessionUtil.getInstance();
		aligenieSession.put(taskQuery.getSessionId(), taskQuery);
	}

	public static TaskQuery getTaskQuery(TaskQuery taskQuery) {
		Map<String, TaskQuery> aligenieSession = AligenieSessionUtil.getInstance();
		return aligenieSession.get(taskQuery.getSessionId());
	}
}
