package com.accenture.ai.model.dingdong;

import java.io.Serializable;

public class DirectiveItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7104606171077955033L;

	private String type;
	private String content;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
