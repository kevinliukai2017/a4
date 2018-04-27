package com.accenture.ai.utils;

import com.accenture.ai.dto.ArticleDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ArticleDTOMapper implements RowMapper<ArticleDTO> {

    @Override
    public ArticleDTO mapRow(ResultSet resultSet, int i) throws SQLException {
//        获取结果集中的数据
        String title = resultSet.getString("title");
        String content = resultSet.getString("content");
        String url = resultSet.getString("url");
        String excerpt = resultSet.getString("excerpt");
//        把数据封装成User对象
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTitle(title);
        articleDTO.setContent(content);
        articleDTO.setUrl(url);
        articleDTO.setExcerpt(excerpt);
        return articleDTO;
    }
}