package com.accenture.ai.utils;

import com.accenture.ai.dto.ArticleDTO;

import java.util.List;

public class ArticleResultContex {

    private List<ArticleDTO> articles;

    public List<ArticleDTO> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleDTO> articles) {
        this.articles = articles;
    }
}
