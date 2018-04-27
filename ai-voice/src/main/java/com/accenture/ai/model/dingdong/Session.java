package com.accenture.ai.model.dingdong;

import java.io.Serializable;
import java.util.Map;

public class Session implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4624894406900316388L;

	private Boolean isNew;
	private String sessionId;
	private Map<String,String> attributes;
	public Boolean getIsNew() {
		return isNew;
	}
	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	
}
