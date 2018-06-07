package com.accenture.ai.service.article.impl;

import com.accenture.ai.dao.article.ArticleDao;
import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.dto.CategoryDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.service.aligenie.WebSocketServiceImpl;
import com.accenture.ai.service.article.ArticleService;
import com.accenture.ai.utils.ArticleResultContex;
import com.accenture.ai.utils.SocketStatusContex;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService{

    private static final LogAgent LOGGER = LogAgent.getLogAgent(ArticleServiceImpl.class);

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    ArticleResultContex articleResultContex;

    @Autowired
    SocketStatusContex socketStatusContex;

    @Autowired
    private WebSocketServiceImpl webSocketServiceImpl;

    @Override
    public List<ArticleDTO> getArticleByWords(String questions, List<String> words) {
        final List<ArticleDTO> articles = articleDao.getArticleByWords(questions, words);

        final List<ArticleDTO> result = rebuildArticleByRelated(articles);

        LOGGER.info("getArticleByWords rebuild result:"+ (new Gson()).toJson(result));

        return result;
    }

    /**
     * rebuild the article to contact with the related articles
     *
     * @param articles
     * @return
     */
    protected List<ArticleDTO> rebuildArticleByRelated(List<ArticleDTO> articles){

        for(ArticleDTO articleDTO : articles){

            if (CollectionUtils.isNotEmpty(articleDTO.getRelatedArticles())){

                //resort the related article
                Collections.sort(articleDTO.getRelatedArticles(), new RelateArticleComparator());

                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(" 您是否对以下内容感兴趣");
                for(ArticleDTO reArticleDTO : articleDTO.getRelatedArticles()){
                    stringBuilder.append(" " + reArticleDTO.getTitle());
                }

                if (StringUtils.isNotEmpty(articleDTO.getExcerpt())){
                    articleDTO.setExcerpt(articleDTO.getExcerpt() + stringBuilder.toString());
                }else{
                    articleDTO.setContent(articleDTO.getContent() + stringBuilder.toString());
                }
            }

        }

        return articles;
    }

    @Override
    public void recordAndSendArticles(String questions, List<ArticleDTO> articleDTOs) {
        if (StringUtils.isNotEmpty(questions) && !CollectionUtils.isEmpty(articleDTOs)){
            LOGGER.info("Start to send articles to socket");

            // send the articles to the socket, so that the frontend page will be redirect to the articles page
            sendArticlesToSocket(questions,articleDTOs);

            LOGGER.info("Start to record articles to context");

            // record the article to the context, and then we could use it in the return back function
            articleResultContex.recordArticles(buildReplyResult(articleDTOs),articleDTOs);
        }
        else {
            if (StringUtils.isEmpty(questions)){
                LOGGER.error("can not record and send articles, because parameters questions is empty");
            }

            if (CollectionUtils.isEmpty(articleDTOs)){
                LOGGER.error("can not record and send articles, because parameters articleDTOs is empty");
            }

        }
    }

    protected void sendArticlesToSocket(String questions, List<ArticleDTO> articleDTOs){

        if (articleDTOs.size() == 1) {
            LOGGER.info("查询到的文章，title：" + articleDTOs.get(0).getTitle() + "  url:"+articleDTOs.get(0).getUrl());
            articleResultContex.setArticles(articleDTOs);
            socketStatusContex.setTitleAndUrl(questions, articleDTOs.get(0).getUrl());
            //send contex to customer client
            webSocketServiceImpl.sendContexToClient();

        } else {
            String titles = "";
            for (ArticleDTO articleDTO : articleDTOs) {
                titles += articleDTO.getTitle() + ",";
            }
            titles += "请问想选择哪一条";
            // help to redirect url by socket(list page)
            articleResultContex.setArticles(articleDTOs);
            socketStatusContex.setTitleAndUrl(questions, "/websocket/articleListFrame");
            //send contex to customer client
            webSocketServiceImpl.sendContexToClient();
        }

    }

    @Override
    public ArticleDTO getArticleDetailFromContext(String title) {
        ArticleDTO result = null;
        if (StringUtils.isNotEmpty(title) && articleResultContex != null && !CollectionUtils.isEmpty(articleResultContex.getArticles())){
            for(ArticleDTO articleDTO : articleResultContex.getArticles()){
                if (title.equals(articleDTO.getTitle())){
                    result = articleDTO;
                }
            }
        }

        if (result == null){
            LOGGER.error("can not get the article detail by the title, the title:"+title);
        }

        return result;
    }

    @Override
    public String buildReplyResult(List<ArticleDTO> articleDTOs) {
        String result = "";
        if (CollectionUtils.isEmpty(articleDTOs)) {
            result = "抱歉没有找到你想要的内容";
        } else if (articleDTOs.size() == 1) {
            if (StringUtils.isNotEmpty(articleDTOs.get(0).getExcerpt())){
                result =  articleDTOs.get(0).getExcerpt();
            }else{
                result = articleDTOs.get(0).getContent();
            }
        } else {
            for (ArticleDTO articleDTO : articleDTOs) {
                result += articleDTO.getTitle() + ",";
            }
            result += "请问想选择哪一条";
        }
        return result;
    }

    @Override
    public List<ArticleDTO> getAndSendArticlesFromContext(String content) {
        LOGGER.info("Start get articles from context, the content:"+content);

        if (StringUtils.isNotEmpty(content)){
            List<ArticleDTO> result = articleResultContex.getArticlesFromRecords(content.replaceAll("_",""));
            if (!CollectionUtils.isEmpty(result)){
                sendArticlesToSocket(content,result);
            }
            else{
                LOGGER.error("Can not return back the pages by the content:[" + content+"],because can't find the articles from the record");
            }

            return result;
        }

        LOGGER.error("Can not return back the pages by the content, because the parameter content is empty");

        return Collections.emptyList();
    }

    @Override
    public List<CategoryDTO> getAllCategories() {

        final List<CategoryDTO> result = articleDao.getAllCategories();
        //resort the related article
        Collections.sort(result, new CategoryArticlesComparator());
        return result;
    }

    /**
     * Comparator to re sort the conditions
     *
     */
    static class RelateArticleComparator implements Comparator
    {
        @Override
        public int compare(final Object object1, final Object object2)
        {
            return new Double(((ArticleDTO) object1).getId())
                    .compareTo(new Double(((ArticleDTO) object2).getId()));
        }
    }

    /**
     * Comparator to re sort the conditions
     *
     */
    static class CategoryArticlesComparator implements Comparator
    {
        @Override
        public int compare(final Object object1, final Object object2)
        {
            return -new Double(((CategoryDTO) object1).getArticles().size())
                    .compareTo(new Double(((CategoryDTO) object2).getArticles().size()));
        }
    }

}
