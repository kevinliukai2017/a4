package com.accenture.ai.service.article;

import com.accenture.ai.dto.ArticleDTO;

import java.util.List;

public interface ArticleService {

    /**
     * get the article by words, will search the article from the database.
     *
     * @param words
     *  the key words
     * @return List<ArticleDTO>
     *  the articles
     */
    List<ArticleDTO> getArticleByWords(String questions, List<String> words);

    /**
     * record the article to context
     *
     * @param questions
     * @param articles
     */
    void recordAndSendArticles(String questions, List<ArticleDTO> articles);

    /**
     * get the detail answer from the context by title
     *
     * @param title
     * @return the detail answer
     */
    ArticleDTO getArticleDetailFromContext(String title);

    /**
     * build the reply title
     *
     * @param articles
     * @return the titles
     */
    String buildReplyResult(List<ArticleDTO> articles);

    /**
     * get the articles from record context and send the articles to the socket server, this method will be use in the
     * return back logic.
     *
     * @param content
     * @return
     */
    List<ArticleDTO> getAndSendArticlesFromContext(String content);
}
