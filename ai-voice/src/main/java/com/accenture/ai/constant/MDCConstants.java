package com.accenture.ai.constant;

public class MDCConstants {
	
	public static final String REQUEST_TRACE_ID_MDC_KEY = "X-B3-TraceId"; // from spring cloud sleuth
	
	public static final String REQUEST_SESSION_ID_MDC_KEY = "req.sessionId";
	public static final String REQUEST_X_FORWARDED_FOR_MDC_KEY = "req.xForwardedFor";
	public static final String REQUEST_CLIENT_IP_MDC_KEY = "req.clientIp";
	public static final String REQUEST_USER_AGENT_MDC_KEY = "req.userAgent";
	public static final String REQUEST_USER_SESSION_ID = "req.userSessionId";
	public static final String REQUEST_ACCESS_CHANNEL = "req.accessChannel";
	
	public static final String CLASS_NAME_MDC_KEY = "app.className";
	public static final String METHOD_NAME_MDC_KEY = "app.methodName";
	
	public static final String RESPONSE_TIME_MDC_KEY = "app.responseTime";
	public static final String DATA_SIZE_MDC_KEY = "app.dataSize";
	
	public static final String REQUEST_RESPONSE_BODY_MDC_KEY = "app.reqOrResp";
	
	public static final String LOG_ENTRY_TYPE_MDC_KEY = "app.logEntryType";
	
	public static final String ERRORCODE_MDC_KEY = "app.errorCode";
	
	public static final String INCOMING_CHANNEL = "incomingChannel";
	public static final String INCOMING_MICROSERVICE = "incomingMicroservice";

	public static final String WP_URL = "http://106.14.124.254:8080/?p=";
	
	
}
