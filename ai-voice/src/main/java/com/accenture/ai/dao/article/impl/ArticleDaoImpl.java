package com.accenture.ai.dao.article.impl;

import com.accenture.ai.dao.article.ArticleDao;
import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.aligenie.SmartDevicePOCServiceImpl;
import com.accenture.ai.utils.ArticleDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleDaoImpl implements ArticleDao {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(ArticleDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final static String QUERY_ARTICLE_BY_QUESTIONS = "SELECT post_title as title, post_content as content, guid as url, post_excerpt as excerpt " +
            "FROM wp_posts WHERE wp_posts.post_status = 'publish' AND wp_posts.post_title = :questions";

    private final static String QUERY_ARTICLE_BY_WORDS = "SELECT post_title as title, post_content as content, guid as url, post_excerpt as excerpt,count(wp_posts.ID) as count, " +
            "wp_terms.name as tag_name FROM wp_posts JOIN wp_term_relationships ON wp_posts.ID=wp_term_relationships.object_id " +
            "JOIN wp_terms ON wp_term_relationships.term_taxonomy_id=wp_terms.term_id " +
            "WHERE wp_posts.post_status = 'publish' AND wp_terms.name IN (:keyWords) " +
            "GROUP BY wp_posts.ID ORDER BY count desc";

    private final static String QUERY_ARTICLE_BY_WORDS_PRE_FIX = "SELECT post_title as title, post_content as content, guid as url, post_excerpt as excerpt,count(wp_posts.ID) as count, " +
            "wp_terms.name as tag_name FROM wp_posts JOIN wp_term_relationships ON wp_posts.ID=wp_term_relationships.object_id " +
            "JOIN wp_terms ON wp_term_relationships.term_taxonomy_id=wp_terms.term_id " +
            "WHERE wp_posts.post_status = 'publish'";

    private final static String QUERY_ARTICLE_BY_WORDS_END_FIX = "GROUP BY wp_posts.ID ORDER BY count desc";

    private final static String QUERY_ALL_TAG = "SELECT name as tag FROM wp_terms";

    private final static String QUERY_TAG_ID_BY_NAME = "SELECT term_id as id FROM wp_terms WHERE wp_terms.name = :tagName";

    private final static String QUERY_ARTICLE_ID_BY_NO = "SELECT ID as id FROM wp_posts WHERE wp_posts.NO = :articleNo";



    @Override
    public List<String> getAllTag() {
        //TODO
        List<String> tags = jdbcTemplate.query(QUERY_ALL_TAG, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                String tag = resultSet.getString("tag");
                return tag;
            }
        });
        return tags;
    }

    @Override
    public List<String> getTagByName(List<String> names) {
        Map<String,Object> paramMap = new HashMap<String, Object>();
        List<String> TagIds = new ArrayList<String>();
        for (String name : names) {
            paramMap.put("tagName", name);
            List<String> tagId = namedParameterJdbcTemplate.query(QUERY_TAG_ID_BY_NAME,paramMap,new RowMapper() {
                @Override
                public String mapRow(ResultSet resultSet, int i) throws SQLException {
                    String id = resultSet.getString("id");
                    return id;
                }
            });
            TagIds.add(tagId.get(0));

        }
            return TagIds;

    }

    @Override
    public List<ArticleDTO> getArticleByWords(String questions, List<String> words) {


        LOGGER.info("ArticleDaoImpl.getArticleByWords questions:"+questions +", key words" + words.toString());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("keyWords",words);
        paramMap.put("questions",questions);

        List<ArticleDTO> result = namedParameterJdbcTemplate.query(QUERY_ARTICLE_BY_QUESTIONS,paramMap,new ArticleDTOMapper());

        if (CollectionUtils.isEmpty(result)){
            result = namedParameterJdbcTemplate.query(QUERY_ARTICLE_BY_WORDS,paramMap,new ArticleDTOMapper());
        }else {
            LOGGER.info("can not get the articles by title, will try to get the articles use the key words");
        }

//        final StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(QUERY_ARTICLE_BY_WORDS_PRE_FIX);
//
//        stringBuilder.append(" AND (wp_terms.name IN (:keyWords) ");
//        for(String word : words){
//            stringBuilder.append("OR post_title LIKE '%" + word + "%' ");
//        }
//        stringBuilder.append(")");
//        stringBuilder.append(QUERY_ARTICLE_BY_WORDS_END_FIX);

        LOGGER.info("ArticleDaoImpl.getArticleByWords, result size()" + result.size());

        return result;
    }

    @Override
    public List<Integer> getArticleIdByNo(String No) {

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("articleNo",No);
        List<Integer> articleIds = new ArrayList<Integer>();
        List<Integer> result = namedParameterJdbcTemplate.query(QUERY_ARTICLE_ID_BY_NO,paramMap,new RowMapper()
        {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                int articleId = resultSet.getInt("id");
                return articleId;
            }
        });
        if(result.size()>0) {
            articleIds.add(result.get(0));
        }
        return articleIds;
    }
}
