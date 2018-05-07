package com.accenture.ai.integration.file.processor;

import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.utils.FileMoveHelper;
import com.accenture.ai.utils.GsonUtils;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import com.accenture.ai.service.article.InsertDataService;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileProcessorTask {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(FileProcessorTask.class);

    private static final String HEADER_FILE_NAME = "file_name";
    private static final String FILE_ORIGINAL_FILE = "file_originalFile";

    @Autowired
    private File inboundRootDirectory;

    @Autowired
    private File inboundProcessingDirectory;

    @Autowired
    private File inboundErrorDirectory;

    @Autowired
    private File inboundArchiveDirectory;

    @Resource
    private InsertDataService insertDataService;


    /**
     * process the import task
     *
     * @param msg
     */
    public void process(Message<List<List<Map<String, String>>>> msg) {
        String fileName = (String) msg.getHeaders().get(HEADER_FILE_NAME);
        File fileOriginalFile = (File) msg.getHeaders().get(FILE_ORIGINAL_FILE);

        List<List<Map<String, String>>> content = msg.getPayload();
        LOGGER.info("The read content is:" + (new Gson()).toJson(content));

        // move the file to processing folder
        fileOriginalFile = moveFileToFolder(fileOriginalFile, inboundProcessingDirectory.getPath());

        // import the articles to the database
        List<List<Map<String, String>>> errorLists = importArticlesFromSheets(content);

        // record the import result
        if (CollectionUtils.isNotEmpty(errorLists)){
            LOGGER.error("There have some error when import the articles");

            // move the file to error folder
            fileOriginalFile = moveFileToFolder(fileOriginalFile, inboundErrorDirectory.getPath());

            //TODO
            // write the error to file
        }else{
            // move the file to archive folder
            fileOriginalFile = moveFileToFolder(fileOriginalFile, inboundArchiveDirectory.getPath());
        }


    }

    /**
     * move the file to folder
     *
     * @param file
     * @param to
     * @return
     */
    protected File moveFileToFolder(File file, String to){
        try {
            return FileMoveHelper.moveFileToFolder(file, inboundProcessingDirectory.getPath());
        } catch (Exception e) {
            LOGGER.error("Failed to move the file from root folder to processing folder. to:"+to);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * import the articles from sheets
     *
     * @param sheets
     * @return
     */
    protected List<List<Map<String, String>>> importArticlesFromSheets(List<List<Map<String, String>>> sheets){

        List<List<Map<String, String>>> errorLists = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sheets)){

            for(List<Map<String, String>> sheet : sheets){
                List<Map<String, String>> errorList = importArticlesFromSheet(sheet);

                if (CollectionUtils.isNotEmpty(errorList)){
                    errorLists.add(errorList);
                }
            }

        }else{
            LOGGER.error("The read excel is empty, can not import the articles");
        }

        return errorLists;

    }

    /**
     * import the articles from sheet
     *
     * @param sheet
     * @return
     */
    protected List<Map<String, String>> importArticlesFromSheet(List<Map<String, String>> sheet){

        List<Map<String, String>> errorList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(sheet)){
            //need to import two sheet , one is for tag ,another is for article
            for(Map<String, String> line : sheet){
                //if article sheet .do this
                this.getInsertDataService().insertArticleData(line);

                //TODO
                // import the article from sheet
            }
            //当所有文章录入后再执行相关文章的关联
            for (Map<String,String> line :sheet){
                this.getInsertDataService().insertReferenceArticle(line);
            }

        }else{
            LOGGER.error("The read excel sheet is empty, can not import the articles");
        }

        return errorList;
    }

    public InsertDataService getInsertDataService() {
        return insertDataService;
    }

    public void setInsertDataService(InsertDataService insertDataService) {
        this.insertDataService = insertDataService;
    }
}
