package com.accenture.ai.controller.mock;

import com.accenture.ai.dto.ArticleDTO;
import com.accenture.ai.utils.ArticleDTOMapper;
import com.accenture.ai.utils.ArticleResultContex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.jdbc.core.JdbcTemplate;

import com.accenture.ai.service.aligenie.WebSocketServiceImpl;
import com.accenture.ai.utils.SocketStatusContex;

import java.util.*;


@Controller
@RequestMapping("/test")
public class TestController {
	
	@Autowired  
	private SocketStatusContex socketStatusContex;

    @Autowired
    ArticleResultContex articleResultContex;

    @Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private WebSocketServiceImpl webSocketServiceImpl;
	
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

        String sql = "SELECT post_title as title, post_content as content, guid as url,count(wp_posts.ID) as count, " +
                "wp_terms.name as tag_name FROM wp_posts JOIN wp_term_relationships ON wp_posts.ID=wp_term_relationships.object_id " +
                "JOIN wp_terms ON wp_term_relationships.term_taxonomy_id=wp_terms.term_id " +
                "WHERE wp_posts.post_status = 'publish' AND wp_terms.name IN (:keyWords) " +
                "GROUP BY wp_posts.ID ORDER BY count desc";
        Map<String, Object> paramMap = new HashMap<String, Object>();

        final List<String> keyWords = new ArrayList<>();
        keyWords.add("出差");
        keyWords.add("报销");
        paramMap.put("keyWords",keyWords);

        List<ArticleDTO> list = namedParameterJdbcTemplate.query(sql,paramMap,new ArticleDTOMapper());

        articleResultContex.setArticles(list);
        socketStatusContex.setTitleAndUrl("文章列表", "/websocket/articleListFrame");
        //send contex to customer client
        webSocketServiceImpl.sendContexToClient();

        String result = "";

        for(ArticleDTO entry : list){
            result += "#################################";
            result += entry.getTitle();
            result += "<br>";
            result += entry.getContent();
            result += "<br>";
            result += "<br>";
        }

        return result;
    }

    @RequestMapping(value = "/article",method = RequestMethod.GET)
    @ResponseBody
    public String getArticle() {

        String sql = "SELECT post_title as title, post_content as content, guid as url,count(wp_posts.ID) as count, " +
                "wp_terms.name as tag_name FROM wp_posts JOIN wp_term_relationships ON wp_posts.ID=wp_term_relationships.object_id " +
                "JOIN wp_terms ON wp_term_relationships.term_taxonomy_id=wp_terms.term_id " +
                "WHERE wp_posts.post_status = 'publish' AND wp_terms.name IN (:keyWords) " +
                "GROUP BY wp_posts.ID ORDER BY count desc";
        Map<String, Object> paramMap = new HashMap<String, Object>();

        final List<String> keyWords = new ArrayList<>();
        keyWords.add("报销");
        paramMap.put("keyWords",keyWords);

        List<ArticleDTO> list = namedParameterJdbcTemplate.query(sql,paramMap,new ArticleDTOMapper());

        articleResultContex.setArticles(Arrays.asList(list.get(0)));
        socketStatusContex.setTitleAndUrl("文章列表", list.get(0).getUrl());
        //send contex to customer client
        webSocketServiceImpl.sendContexToClient();

        String result = "";

        for(ArticleDTO entry : list){
            result += "#################################";
            result += entry.getTitle();
            result += "<br>";
            result += entry.getContent();
            result += "<br>";
            result += "<br>";
        }

        return result;
    }

    @RequestMapping(value = "/success",method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "success";
    }

}
