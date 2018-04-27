package com.accenture.ai.utils;

import java.lang.reflect.Type;

import com.accenture.ai.model.dingdong.TaskQuery;
import com.alibaba.da.coin.ide.spi.trans.GsonUtils;
import com.google.gson.reflect.TypeToken;

public class MetaFormat {
	public static TaskQuery parseToQuery(String requestMetaData) {
		Type type = (new TypeToken() {
		}).getType();
		TaskQuery query = (TaskQuery) GsonUtils.gson.fromJson(requestMetaData, type);
		return query;
	}
}
