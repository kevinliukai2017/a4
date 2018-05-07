package com.accenture.ai.utils;

import java.lang.reflect.Type;

import com.accenture.ai.model.dingdong.TaskQuery;
import com.alibaba.da.coin.ide.spi.trans.GsonUtils;
import com.google.gson.reflect.TypeToken;

public class MetaFormat {
	public static TaskQuery parseToQuery(String requestMetaData) {
		Type type = new TypeToken<TaskQuery>(){}.getType();
		TaskQuery query = (TaskQuery) GsonUtils.gson.fromJson(requestMetaData, type);
		return query;
	}
	
//	public static void main(String[] args) {
//		String t = "{  \"versionid\": \"1.0\",  \"status\": \"NOTICE\",  \"sequence\": \"1749f49a90f94dfe9019789b3de44ec5\",  \"timestamp\": 1525659479824,  \"application_info\": {    \"application_id\": \"68kayukl\",    \"application_name\": \"Test\",    \"application_version\": \"10000\"  },  \"session\": {    \"is_new\": true,    \"session_id\": \"b1a266a62a24430a8809a37fb1165272\",    \"attributes\": {      \"value\": \"一\",      \"focus\": \"Test\",      \"bizname\": \"小哲同学\",      \"type\": \"sequence\"    }  },  \"user\": {    \"user_id\": \"620d8e977d1a4d56af44de44b17d63a9\",    \"attributes\": {}  },  \"input_text\": \"让小哲同学选择第一条\",  \"notice_type\": \"DEV_SERVICE_RESP_PACK_ERROR\",  \"extend\": {}}";
//		TaskQuery a = parseToQuery(t);
//		System.out.println("ttt");
//	}
}
