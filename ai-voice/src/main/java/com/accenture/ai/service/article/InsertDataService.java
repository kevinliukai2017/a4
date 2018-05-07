package com.accenture.ai.service.article;

import java.util.Map;

/**
 * @Author: havey
 * @Discription:
 * @Date: Created in 9:41 PM 2018/5/5
 **/
public interface InsertDataService {
    /**
        * @Author: liruilin
        * @Discription:
        * @Date: Created in 9:42 PM 2018/5/5
        * @Param: Map<String,String> article
    **/
    String insertArticleData(Map<String,String> article);

    String insertTagData(String tag);


}
