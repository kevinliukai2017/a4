package com.accenture.ai.utils;

public class SocketStatusContex {
	
	private String title;
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setTitleAndUrl(String title, String url) {
		this.title = title;
		this.url = url;
	}

}
