package com.accenture.ai.utils;

import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.dto.CategoryDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.article.ArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleResultContex {
    private static final LogAgent LOGGER = LogAgent.getLogAgent(ArticleResultContex.class);

    @Autowired
    ArticleService articleService;

    private List<ArticleDTO> articles;

    private List<CategoryDTO> categories;

    private Map<String,List<ArticleDTO>> recordArticles;

    public List<ArticleDTO> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleDTO> articles) {
        this.articles = articles;
    }

    public Map<String, List<ArticleDTO>> getRecordArticles() {
        if (recordArticles == null){
            recordArticles = new HashMap<>();
        }

        return recordArticles;
    }

    public void setRecordArticles(Map<String, List<ArticleDTO>> recordArticles) {
        this.recordArticles = recordArticles;
    }

    public void recordArticles(String reply, List<ArticleDTO> articles){
        if (StringUtils.isNotEmpty(reply) && !CollectionUtils.isEmpty(articles)){
            getRecordArticles().put(reply,articles);
        }
        else{
            LOGGER.error("can not record the articles, because the parameter is empty");
        }
    }

    public List<ArticleDTO> getArticlesFromRecords(String content){
        return getRecordArticles().containsKey(content) ? getRecordArticles().get(content) : Collections.emptyList();
    }

    public List<CategoryDTO> getCategories() {

        if (CollectionUtils.isEmpty(categories)){
            LOGGER.error("Go to get the all categories");
            categories = articleService.getAllCategories();
        }
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }
}
