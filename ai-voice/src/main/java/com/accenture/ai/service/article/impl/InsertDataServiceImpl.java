package com.accenture.ai.service.article.impl;

import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.article.InsertDataService;
import com.oracle.tools.packager.Log;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

/**
 * @Author: havey
 * @Discription:
 * @Date: Created in 9:43 PM 2018/5/5
 * @Modified By:
 **/
public class InsertDataServiceImpl implements InsertDataService {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(InsertDataServiceImpl.class);


    @Override
    public String insertTagData(Map<String, String> tag) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://rm-bp1ymf161p7o36y2lto.mysql.rds.aliyuncs.com:3306/transa4?characterEncoding=utf8&useSSL=false", "transa4", "aiTP2018(]");
            //for tag tabel
            String tagSql = "insert into wp_terms(name) values(?)";
            PreparedStatement staTag = con.prepareStatement(tagSql);
            staTag.setString(1, tag.get("TAG"));
            int rows = staTag.executeUpdate();
            //for tag reference
            String tagrReference = "insert into wp_term_taxonomy(term_taxonomy_id,term_id,taxonomy) values(?,?,?)";

            PreparedStatement staTagRe = con.prepareStatement(tagrReference);
            staTagRe.setString(1,tag.get("TAG_ID"));
            staTagRe.setString(2,tag.get("TAG_ID"));
            staTagRe.setString(3,"post_tag");

            int ReferenceRows = staTagRe.executeUpdate();

            if (rows > 0 && ReferenceRows >0) {

                LOGGER.info("operate successfully!");

            }

            staTag.close();
            staTagRe.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    @Override
    public String insertArticleData(Map<String, String> article) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://rm-bp1ymf161p7o36y2lto.mysql.rds.aliyuncs.com:3306/transa4?characterEncoding=utf8&useSSL=false", "transa4", "aiTP2018(]");
            // for article id and title and content
            String articleSql = "insert into wp_posts(ID,post_title,post_content) values(?,?,?)";

            PreparedStatement sta = con.prepareStatement(articleSql);

            sta.setString(1, article.get("NO"));

            sta.setString(2, article.get("TITLE"));

            sta.setString(3, article.get("CONTENT"));

            //for reference article
            String referenceSql = "insert into wp_yarpp_related_cache(ID,reference_ID,score) values(?,?,?)";
            PreparedStatement staReference = con.prepareStatement(referenceSql);
            staReference.setString(1, article.get("NO"));
            staReference.setString(2, article.get("REFERENCENO"));
            staReference.setString(3, "2");  //score default value is 2

            //for article tag

            String tag="insert into wp_term_relationships(object_id，term_taxonomy_id) values(?,？)";
            PreparedStatement staTag = con.prepareStatement(tag);
            staTag.setString(1,article.get("NO"));
            staTag.setString(2,article.get("TAG"));





            int rows = sta.executeUpdate();
            int referenceRows = staReference.executeUpdate();
            int TagRows = staTag.executeUpdate();

            if (rows > 0 && referenceRows>0 && TagRows>0) {

                LOGGER.info("operate successfully!");

            }

            sta.close();
            staReference.close();
            staTag.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }
}