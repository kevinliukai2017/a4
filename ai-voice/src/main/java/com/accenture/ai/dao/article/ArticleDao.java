package com.accenture.ai.dao.article;

import com.accenture.ai.dto.ArticleDTO;

import java.util.List;

public interface ArticleDao {

    /**
     * get the article by words, will search the article from the database.
     *
     * @param words
     *  the key words
     * @return List<ArticleDTO>
     *  the articles
     */
    List<ArticleDTO> getArticleByWords(List<String> words);
}
