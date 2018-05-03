package com.accenture.ai.dto;

public class ArticleDTO {

	private String title;
	private String content;
	private String excerpt;
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() { return url; }

	public void setUrl(String url) { this.url = url; }

	public String getExcerpt() {
		return excerpt;
	}

	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}
}
