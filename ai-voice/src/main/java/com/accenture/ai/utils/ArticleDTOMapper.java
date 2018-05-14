package com.accenture.ai.utils;

import com.accenture.ai.dto.ArticleDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ArticleDTOMapper implements RowMapper<Map<String,Object>> {

    @Override
    public Map<String,Object> mapRow(ResultSet resultSet, int i) throws SQLException {

        Map<String,Object> articleMap = new HashMap<>();
        articleMap.put("id",resultSet.getLong("id"));
        articleMap.put("title",resultSet.getString("title"));
        articleMap.put("content",resultSet.getString("content"));
        articleMap.put("url",resultSet.getString("url"));
        articleMap.put("excerpt",resultSet.getString("excerpt"));

        articleMap.put("re_id",resultSet.getLong("re_id"));
        articleMap.put("re_title",resultSet.getString("re_title"));
        articleMap.put("re_content",resultSet.getString("re_content"));
        articleMap.put("re_url",resultSet.getString("re_url"));
        articleMap.put("re_excerpt",resultSet.getString("re_excerpt"));

        return articleMap;
    }
}