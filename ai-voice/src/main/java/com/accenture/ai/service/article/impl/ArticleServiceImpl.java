package com.accenture.ai.service.article.impl;

import com.accenture.ai.dao.article.ArticleDao;
import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.service.article.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService{

    @Autowired
    private ArticleDao articleDao;

    @Override
    public List<ArticleDTO> getArticleByWords(List<String> words) {
        return articleDao.getArticleByWords(words);
    }
}
