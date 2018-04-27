package com.accenture.ai.model.dingdong;

import java.io.Serializable;

public class Card implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4151712718302107027L;

	private String title;
	private String type;
	private String text;
	private String url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
