package com.accenture.ai.model.dingdong;

import java.io.Serializable;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4773239742911563106L;

	@SerializedName("user_id")
	private String userId;
	private Map<String,String> attributes;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	
}
