package com.accenture.ai.dao.article;

import com.accenture.ai.dto.ArticleDTO;

import java.util.List;

public interface ArticleDao {

    /**
     * get the article by words, will search the article from the database.
     *
     * @param questions
     *  the questions
     * @param words
     *  the key words
     * @return List<ArticleDTO>
     *  the articles
     */
    List<ArticleDTO> getArticleByWords(String questions, List<String> words);

    List<String> getAllTag();

    List<String> getTagByName(List<String> name);

    List<Integer> getArticleIdByNo(String No);
}
