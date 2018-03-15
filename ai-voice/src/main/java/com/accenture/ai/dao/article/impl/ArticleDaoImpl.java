package com.accenture.ai.dao.article.impl;

import com.accenture.ai.dao.article.ArticleDao;
import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.aligenie.SmartDevicePOCServiceImpl;
import com.accenture.ai.utils.ArticleDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleDaoImpl implements ArticleDao {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(SmartDevicePOCServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final static String QUERY_ARTICLE_BY_WORDS = "SELECT post_title as title, post_content as content, guid as url,count(wp_posts.ID) as count, " +
            "wp_terms.name as tag_name FROM wp_posts JOIN wp_term_relationships ON wp_posts.ID=wp_term_relationships.object_id " +
            "JOIN wp_terms ON wp_term_relationships.term_taxonomy_id=wp_terms.term_id " +
            "WHERE wp_posts.post_status = 'publish' AND wp_terms.name IN (:keyWords) " +
            "GROUP BY wp_posts.ID ORDER BY count desc";

    @Override
    public List<ArticleDTO> getArticleByWords(List<String> words) {


        LOGGER.info("ArticleDaoImpl.getArticleByWords, key words" + words.toString());

        Map<String, Object> paramMap = new HashMap<String, Object>();

        final List<String> keyWords = new ArrayList<>();
        keyWords.add("出差");
        paramMap.put("keyWords",keyWords);

        List<ArticleDTO> list = namedParameterJdbcTemplate.query(QUERY_ARTICLE_BY_WORDS,paramMap,new ArticleDTOMapper());

        LOGGER.info("ArticleDaoImpl.getArticleByWords, result size()" + list.size());

        return list;
    }
}
