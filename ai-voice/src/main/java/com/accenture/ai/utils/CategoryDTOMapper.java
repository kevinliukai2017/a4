package com.accenture.ai.utils;

import com.accenture.ai.logging.LogAgent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CategoryDTOMapper implements RowMapper<Map<String,Object>> {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(CategoryDTOMapper.class);

    @Override
    public Map<String,Object> mapRow(ResultSet resultSet, int i) throws SQLException {

        Map<String,Object> categoryMap = new HashMap<>();
        categoryMap.put("category_id",resultSet.getLong("category_id"));
        categoryMap.put("category_name",resultSet.getString("category_name"));

        categoryMap.put("article_id",resultSet.getLong("article_id"));
        categoryMap.put("article_title",resultSet.getString("article_title"));
        categoryMap.put("article_content",resultSet.getString("article_content"));
        categoryMap.put("article_url",resultSet.getString("article_url"));
        categoryMap.put("article_excerpt",resultSet.getString("article_excerpt"));

        return categoryMap;
    }
}