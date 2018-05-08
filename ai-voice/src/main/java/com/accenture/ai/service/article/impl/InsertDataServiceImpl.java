package com.accenture.ai.service.article.impl;

import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.article.InsertDataService;

import org.springframework.stereotype.Component;

import com.accenture.ai.dao.article.ArticleDao;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

import com.accenture.ai.constant.MDCConstants;

/**
 * @Author: havey
 * @Discription:
 * @Date: Created in 9:43 PM 2018/5/5
 * @Modified By:
 **/
@Service
public class InsertDataServiceImpl implements InsertDataService {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(InsertDataServiceImpl.class);

    @Resource
    ArticleDao articleDao;
    @Override
    public String insertTagData(String tag) {
        List<String> tags = new ArrayList<>();
        tags.add(tag);
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://rm-bp1ymf161p7o36y2lto.mysql.rds.aliyuncs.com:3306/transa4?characterEncoding=utf8&useSSL=false", "transa4", "aiTP2018(]");
            //for tag tabel
            String tagSql = "insert into wp_terms(name) values(?)";
            PreparedStatement staTag = con.prepareStatement(tagSql);
            staTag.setString(1, tag);
            int rows = staTag.executeUpdate();

            //for tag reference
            String tagrReference = "insert into wp_term_taxonomy(term_taxonomy_id,term_id,taxonomy) values(?,?,?)";

            PreparedStatement staTagRe = con.prepareStatement(tagrReference);
            staTagRe.setString(1,articleDao.getTagByName(tags).get(0));
            staTagRe.setString(2,articleDao.getTagByName(tags).get(0));
            staTagRe.setString(3,"post_tag");

            int ReferenceRows = staTagRe.executeUpdate();
            if(ReferenceRows >0) {
                LOGGER.info("regerenceTag operate successfully");
            }
            if (rows > 0 ) {

                LOGGER.info("operate successfully!");

            }

            staTag.close();
           // staTagRe.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    @Override
    public String insertArticleData(Map<String, String> article) {
        if (this.isTagExsit(article)) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://rm-bp1ymf161p7o36y2lto.mysql.rds.aliyuncs.com:3306/transa4?characterEncoding=utf8&useSSL=false", "transa4", "aiTP2018(]");
            // for article id and title and content

            String articleSql = "insert into wp_posts(NO,post_title,post_content) values(?,?,?)";

            PreparedStatement sta = con.prepareStatement(articleSql);

            //sta.setString(1, article.get("No"));
            //double doubleID = Double.valueOf(article.get("No"));
           // int id = (int)doubleID;
            sta.setString(1,article.get("No"));
            sta.setString(2, article.get("Title"));

            sta.setString(3, article.get("Content"));
            int rows = sta.executeUpdate();
            if ( rows > 0) {

                LOGGER.info("Article import operate successfully!");

            }
            sta.close();



            List<Integer> articleId = articleDao.getArticleIdByNo(article.get("No"));
            //insert for article url
            String prefixUrl = MDCConstants.WP_URL;
            String url = "update wp_posts set guid = ? WHERE ID = ?";
            PreparedStatement staUrl = con.prepareStatement(url);
            staUrl.setString(1,prefixUrl+articleId.get(0));
            staUrl.setInt(2,articleId.get(0));
            int RulRows = staUrl.executeUpdate();
            if (RulRows > 0){
                LOGGER.info("Guid import operate successfully!");
            }
            //for article tag
            List<String> TagIds = this.getArticleDao().getTagByName(Arrays.asList(article.get("Tag").split(",")));
            for(String tagId : TagIds) {
                String tag = "insert into wp_term_relationships(object_id,term_taxonomy_id) values(?,?)";
                PreparedStatement staTag = con.prepareStatement(tag);

                staTag.setInt(1, articleId.get(0));
                double DoubletagID = Double.valueOf(tagId);
                int tagID = (int)DoubletagID;
                staTag.setInt(2, tagID);
                int TagRows = staTag.executeUpdate();
                //int rows = sta.executeUpdate();
               // int referenceRows = staReference.executeUpdate();


                if ( TagRows > 0) {

                    LOGGER.info("Tag import operate successfully!");

                }

                staTag.close();
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

        return null;
    }

    @Override
    public String insertReferenceArticle(Map<String, String> article) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://rm-bp1ymf161p7o36y2lto.mysql.rds.aliyuncs.com:3306/transa4?characterEncoding=utf8&useSSL=false", "transa4", "aiTP2018(]");
            // for article id and title and content
            List<Integer> articleId = articleDao.getArticleIdByNo(article.get("No"));
            for (String referenceNo : Arrays.asList(article.get("ReferenceNo").split(","))) {
                List<Integer> referenceId = articleDao.getArticleIdByNo(referenceNo);
                if(!CollectionUtils.isEmpty(referenceId)){
                String referenceSql = "insert into wp_yarpp_related_cache(ID,reference_ID,score) values(?,?,?)";
                PreparedStatement staReference = con.prepareStatement(referenceSql);

                staReference.setInt(1, articleId.get(0));
                // double doubleReID = Double.valueOf(referenceNo);
                // int ReID = (int)doubleReID;
                //to modify
                staReference.setInt(2, referenceId.get(0));
                staReference.setFloat(3, 2);  //score default value is 2

                int referenceRows = staReference.executeUpdate();
                if (referenceRows > 0) {

                    LOGGER.info("reference article operate successfully!");

                }
                staReference.close();
            }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean isTagExsit(Map<String,String> article){
        List<String> tags= this.getArticleDao().getAllTag();
        List<String> existTags = Arrays.asList(article.get("Tag").split(","));
        for (String tag : existTags) {
            if (!tags.contains(tag)) {
                this.insertTagData(tag);
            }
        }
        return true;
    }

    public ArticleDao getArticleDao() {
        return articleDao;
    }

    public void setArticleDao(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }
}