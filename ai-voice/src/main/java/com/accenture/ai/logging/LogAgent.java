package com.accenture.ai.logging;


import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.accenture.ai.constant.MDCConstants;

import net.logstash.logback.argument.StructuredArguments;

public class LogAgent {
	
	public static final String LOG_ENTRY_TYPE_INT = "INT"; // interface
	public static final String LOG_ENTRY_TYPE_PER = "PER"; // performance
	public static final String LOG_ENTRY_TYPE_ERR = "ERR"; // error
	public static final String LOG_ENTRY_TYPE_EVT = "EVT"; // event
	public static final String LOG_ENTRY_TYPE_INF = "INF";
	
	private Logger LOGGER;
	private Class<?> clazz;
	private String className;
	
	private LogAgent(Class<?> clazz){
		this.clazz = clazz;
		this.className = clazz.getName();
		this.LOGGER = (Logger)LoggerFactory.getLogger(clazz);
	}
	
	public static LogAgent getLogAgent(Class<?> clazz){
		return new LogAgent(clazz);
	}
	
	/* 
	 * Designates fine-grained informational events that are most useful to debug an application.
	 */
	public void debug(String message){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_INF);
		LOGGER.debug(message);
		clearMDC();
	}
	
	public void debug(String message, Throwable exceptionObject){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_INF);
		LOGGER.debug(message, exceptionObject);
		clearMDC();
	}
	
	/*
	 * Designates informational messages that highlight the progress of the application at coarse-grained level.
	 */
	
	public void info(String message){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_INF);	
		LOGGER.info(message);
		clearMDC();
	}
	
	/* for each interface request only */
	public void info(String methodName, String message){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_INF);
		MDC.put(MDCConstants.METHOD_NAME_MDC_KEY, methodName);
		LOGGER.info(message);
		clearMDC();
	}
	
	// log the interface message request and response
	public void info(String methodName,String requestMsg,int requestMsgSize, String message){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_INT);
		MDC.put(MDCConstants.METHOD_NAME_MDC_KEY, methodName);
		LOGGER.info(message, StructuredArguments.kv(MDCConstants.REQUEST_RESPONSE_BODY_MDC_KEY, requestMsg), 
				StructuredArguments.kv(MDCConstants.DATA_SIZE_MDC_KEY, requestMsgSize));
		clearMDC();
	}
	
	// log the performance
	public void info(String methodName, long startTime, String logMsg){

		String msg = "[TimeDetails][" + logMsg + "][Time: " + DurationFormatUtils.formatDuration(startTime, "HH:mm:ss,SSS") + "]";
		
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_PER);
		MDC.put(MDCConstants.METHOD_NAME_MDC_KEY, methodName);
	 	LOGGER.info(msg, StructuredArguments.kv(MDCConstants.RESPONSE_TIME_MDC_KEY, startTime));
		clearMDC();
	}
	
	/*public void info(String methodName, long startTime){
		this.info(methodName, startTime, "[" + className + "][" + methodName + "]");
	}*/
	
	/*
	 * Designates potentially harmful situations.
	 */	
	public void warn(String message, String errorCode){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_ERR);
		LOGGER.warn(message, StructuredArguments.kv(MDCConstants.ERRORCODE_MDC_KEY, errorCode));
		clearMDC();
	}
	
	public void warn(String message, Exception e){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_ERR);
		LOGGER.warn(message, e);
		clearMDC();
	}
	
	public void warn(String message){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_ERR);	
		LOGGER.warn(message);
		clearMDC();
	}
	
	/*
	 * Designates error events that might still allow the application to continue running.
	 */
	public void error(String message, String errorCode, Exception e){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_ERR);
		LOGGER.error(message, StructuredArguments.kv(MDCConstants.ERRORCODE_MDC_KEY, errorCode), e);
		clearMDC();
	}
	
	public void error(String message, String errorCode){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_ERR);
		LOGGER.error(message, StructuredArguments.kv(MDCConstants.ERRORCODE_MDC_KEY, errorCode));
		clearMDC();
	}
	
	public void error(String message, Exception e){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_ERR);
		LOGGER.error(message, e);
		clearMDC();
	}
	
	public void error(String message){
		MDC.put(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY, LOG_ENTRY_TYPE_ERR);
		LOGGER.error(message);
		clearMDC();
	}
	
	private void clearMDC() {
		MDC.remove(MDCConstants.LOG_ENTRY_TYPE_MDC_KEY);
		MDC.remove(MDCConstants.METHOD_NAME_MDC_KEY);
		//MDC.remove(REQUEST);
		MDC.remove(MDCConstants.DATA_SIZE_MDC_KEY);
		MDC.remove(MDCConstants.REQUEST_RESPONSE_BODY_MDC_KEY);
		MDC.remove(MDCConstants.RESPONSE_TIME_MDC_KEY);
		MDC.remove(MDCConstants.ERRORCODE_MDC_KEY);
	}
}
