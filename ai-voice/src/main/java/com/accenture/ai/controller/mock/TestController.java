package com.accenture.ai.controller.mock;

import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.utils.ArticleDTOMapper;
import com.accenture.ai.utils.ArticleResultContex;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.accenture.ai.service.aligenie.WebSocketServiceImpl;
import com.accenture.ai.utils.SocketStatusContex;

import java.util.*;


@Controller
@RequestMapping("/test")
public class TestController {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(TestController.class);
    private static final String ARTICLE_DETAIL_URL = "/websocket/articleDetailFrame";
	
	@Autowired  
	private SocketStatusContex socketStatusContex;

    @Autowired
    ArticleResultContex articleResultContex;

    @Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private WebSocketServiceImpl webSocketServiceImpl;

    private static final Gson GSON = new Gson();
	
	@RequestMapping(value = "/changeStatus",method = RequestMethod.POST)
    @ResponseBody
    public String changeStatus(final String title, final String url) {
		socketStatusContex.setTitleAndUrl(title, url);
		
		//send contex to customer client
		webSocketServiceImpl.sendContexToClient();
		
		return "success";
    }

    @RequestMapping(value = "/articles",method = RequestMethod.GET)
    @ResponseBody
    public String getAllArticles() {

        String sql = "SELECT article1.ID as id, article1.post_title as title, article1.post_content as content, article1.guid as url, " +
                "article1.post_excerpt as excerpt, count(article1.ID) as count, " +
                "wp_terms.name as tag_name, article2.ID as re_id, article2.post_title as re_title, article2.post_content as re_content, article2.guid as re_url, article2.post_excerpt as re_excerpt " +
                "FROM wp_posts AS article1 JOIN wp_term_relationships ON article1.ID=wp_term_relationships.object_id " +
                "LEFT JOIN wp_terms ON wp_term_relationships.term_taxonomy_id=wp_terms.term_id " +
                "LEFT JOIN wp_yarpp_related_cache ON article1.ID=wp_yarpp_related_cache.reference_ID " +
                "LEFT JOIN wp_posts AS article2 ON article2.ID=wp_yarpp_related_cache.ID " +
                "WHERE article1.post_status = 'publish' AND wp_terms.name IN (:keyWords) " +
                "GROUP BY article1.ID,article2.ID ORDER BY count desc";
        Map<String, Object> paramMap = new HashMap<String, Object>();

        final List<String> keyWords = new ArrayList<>();
        keyWords.add("公司项目");
        keyWords.add("立白");
        paramMap.put("keyWords",keyWords);

        List<Map<String,Object>> articleMap = namedParameterJdbcTemplate.query(sql,paramMap,new ArticleDTOMapper());
        final List<ArticleDTO> list = populateArticles(articleMap);

        articleResultContex.setArticles(list);
        socketStatusContex.setTitleAndUrl("文章列表", "/websocket/articleListFrame");
        //send contex to customer client
        webSocketServiceImpl.sendContexToClient();

        String result = (new Gson()).toJson(list);

        return result;
    }

    @RequestMapping(value = "/article",method = RequestMethod.GET)
    @ResponseBody
    public String getArticle() {

        String sql = "SELECT article1.ID as id, article1.post_title as title, article1.post_content as content, article1.guid as url, article1.post_excerpt as excerpt, " +
                "article2.ID as re_id, article2.post_title as re_title, article2.post_content as re_content, article2.guid as re_url, article2.post_excerpt as re_excerpt " +
                "FROM wp_posts AS article1 LEFT JOIN wp_yarpp_related_cache ON article1.ID=wp_yarpp_related_cache.reference_ID " +
                "LEFT JOIN wp_posts AS article2 ON article2.ID=wp_yarpp_related_cache.ID WHERE article1.post_status = 'publish' AND article1.post_title = :questions";
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("questions","项目目前进度怎么样");

        LOGGER.info("sql:" + sql + " param:" + paramMap);

        List<Map<String,Object>> articleMap = namedParameterJdbcTemplate.query(sql,paramMap,new ArticleDTOMapper());

        final List<ArticleDTO> list = populateArticles(articleMap);

        articleResultContex.setArticles(Arrays.asList(list.get(0)));
        socketStatusContex.setTitleAndUrl("文章列表", ARTICLE_DETAIL_URL);
        //send contex to customer client
        webSocketServiceImpl.sendContexToClient();

        String result = (new Gson()).toJson(list);

        return "query result:"+(new Gson()).toJson(articleMap) + ", populate result:" + result;
    }

    /**
     * populate the articles to DTO
     *
     * @param articleMap
     * @return
     */
    protected List<ArticleDTO> populateArticles(List<Map<String,Object>> articleMap){

        final Map<Long,ArticleDTO> result = new HashMap<>();

        for(Map<String,Object> entry: articleMap){
            if (entry.containsKey("id")){
                final Long main_id = (Long)entry.get("id");
                if (result.containsKey(main_id)){

                    if (!Objects.isNull(entry.get("re_id"))
                            && StringUtils.isNotEmpty((String)entry.get("re_url"))
                            && StringUtils.isNotEmpty((String)entry.get("re_url"))
                            && StringUtils.isNotEmpty((String)entry.get("re_title"))
                            && StringUtils.isNotEmpty((String)entry.get("re_content"))){
                        final ArticleDTO relatedArticle = new ArticleDTO();
                        relatedArticle.setId((Long)entry.get("re_id"));
                        relatedArticle.setUrl((String)entry.get("re_url"));
                        relatedArticle.setExcerpt((String)entry.get("re_excerpt"));
                        relatedArticle.setTitle((String)entry.get("re_title"));
                        relatedArticle.setContent((String)entry.get("re_content"));
                        result.get(main_id).getRelatedArticles().add(relatedArticle);
                    }

                }else{
                    final ArticleDTO mainArticle = new ArticleDTO();

                    mainArticle.setId(main_id);
                    mainArticle.setUrl((String)entry.get("url"));
                    mainArticle.setExcerpt((String)entry.get("excerpt"));
                    mainArticle.setTitle((String)entry.get("title"));
                    mainArticle.setContent((String)entry.get("content"));

                    final List<ArticleDTO> relatedArticles = new ArrayList<>();

                    if (!Objects.isNull(entry.get("re_id"))
                            && StringUtils.isNotEmpty((String)entry.get("re_url"))
                            && StringUtils.isNotEmpty((String)entry.get("re_url"))
                            && StringUtils.isNotEmpty((String)entry.get("re_title"))
                            && StringUtils.isNotEmpty((String)entry.get("re_content"))){
                        final ArticleDTO relatedArticle = new ArticleDTO();
                        relatedArticle.setId((Long)entry.get("re_id"));
                        relatedArticle.setUrl((String)entry.get("re_url"));
                        relatedArticle.setExcerpt((String)entry.get("re_excerpt"));
                        relatedArticle.setTitle((String)entry.get("re_title"));
                        relatedArticle.setContent((String)entry.get("re_content"));
                        relatedArticles.add(relatedArticle);
                    }

                    mainArticle.setRelatedArticles(relatedArticles);

                    result.put(main_id,mainArticle);
                }
            }
        }

        return new ArrayList<>(result.values());
    }

    @RequestMapping(value = "/success",method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "success";
    }

    @RequestMapping(value = "/articlesRecordsContext",method = RequestMethod.GET)
    @ResponseBody
    public String getArticlesRecordsContext(){
	    return GSON.toJson(articleResultContex.getRecordArticles());
    }

    @RequestMapping(value = "/removeCategories",method = RequestMethod.GET)
    @ResponseBody
    public String removeCategories(){
        articleResultContex.setCategories(Collections.emptyList());
        return GSON.toJson(articleResultContex);
    }

    @RequestMapping(value = "/articlesContext",method = RequestMethod.GET)
    @ResponseBody
    public String getArticlesContext(){
        return GSON.toJson(articleResultContex.getArticles());
    }

}
