package com.accenture.ai.dto;

import java.util.List;

public class ArticleDTO {

    private Long id;
	private String title;
	private String content;
	private String excerpt;
	private String url;
	private List<ArticleDTO> relatedArticles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

	public List<ArticleDTO> getRelatedArticles() {
		return relatedArticles;
	}

	public void setRelatedArticles(List<ArticleDTO> relatedArticles) {
		this.relatedArticles = relatedArticles;
	}
}
