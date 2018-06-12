package com.accenture.ai.integration.file.processor;

import com.accenture.ai.constant.ArticleConstants;
import com.accenture.ai.dto.ArticleImportErrorData;
import com.accenture.ai.logging.LogAgent;
import com.accenture.ai.utils.ExcelWriteHelper;
import com.accenture.ai.utils.FileMoveHelper;
import com.accenture.ai.utils.GsonUtils;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import com.accenture.ai.service.article.InsertDataService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
        fileOriginalFile = moveFileToFolder(fileOriginalFile, inboundProcessingDirectory.getPath(),fileOriginalFile.getName());

        // import the articles to the database
        Map<Long,List<ArticleImportErrorData>> errorLists = importArticlesFromSheets(content);

        // record the import result
        if (MapUtils.isNotEmpty(errorLists)){
            LOGGER.error("There have some error when import the articles");

            // move the file to error folder
            fileOriginalFile = writeErrorMessageToFile(fileOriginalFile, errorLists);

        }else{
            LOGGER.info("Import articles success");
            // move the file to archive folder
            fileOriginalFile = archiveFile(fileOriginalFile);
        }

    }

    /**
     * write the error message to file
     *
     * @param file
     * @param errorLists
     * @return
     */
    protected File writeErrorMessageToFile(File file, Map<Long,List<ArticleImportErrorData>> errorLists){

        try {
            // write the error message
            ExcelWriteHelper.writeExcel(file.getPath(),errorLists);

            //move the file
            return moveFileToFolder(file,inboundErrorDirectory.getPath(),file.getName()+ (new Date()).getTime() + "error");
        } catch (IOException e) {
            LOGGER.error("Failed to write the error message to file");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * archive the imported file
     *
     * @param file
     * @return
     */
    protected File archiveFile(File file){
        return moveFileToFolder(file,inboundArchiveDirectory.getPath(),file.getName() + (new Date()).getTime());
    }

    /**
     * move the file to folder
     *
     * @param file
     * @param to
     * @param fileName
     * @return
     */
    protected File moveFileToFolder(File file, String to, String fileName){
        try {
            return FileMoveHelper.moveFileToFolder(file, to, fileName);
        } catch (Exception e) {
            LOGGER.error("Failed to move the file from root folder to processing folder. to:" + to);
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
    protected Map<Long,List<ArticleImportErrorData>> importArticlesFromSheets(List<List<Map<String, String>>> sheets){

        Map<Long,List<ArticleImportErrorData>> errorLists = new HashMap<>();

        if (CollectionUtils.isEmpty(sheets)){
            LOGGER.error("The read excel is empty, can not import the articles");
            return errorLists;
        }

        int i = 0;
        for(List<Map<String, String>> sheet : sheets){
            List<ArticleImportErrorData> errorList = importArticlesFromSheet(sheet);

            if (CollectionUtils.isNotEmpty(errorList)){
                errorLists.put(Long.valueOf(i),errorList);
            }

            i++;
        }

        return errorLists;

    }

    /**
     * import the articles from sheet
     *
     * @param sheet
     * @return
     */
    protected List<ArticleImportErrorData> importArticlesFromSheet(List<Map<String, String>> sheet){

        List<ArticleImportErrorData> errorList = new ArrayList<>();

        if (CollectionUtils.isEmpty(sheet)){
            LOGGER.error("The read excel sheet is empty, can not import the articles");
            return errorList;
        }

        final List<Map<String, String>> leftLines = new ArrayList<>();

        //need to import two sheet , one is for tag ,another is for article
        int i = 0;
        for(Map<String, String> line : sheet){
            i++;

            // validate the line
            String validateResult = validateLine(line);
            if (StringUtils.isNotEmpty(validateResult)){
                LOGGER.error("Fail to validate the line number:" + i + "line:" + line + " errorMessage:" + validateResult);
                ArticleImportErrorData errorData = buildImportErrorData(line,validateResult,Long.valueOf(i));
                errorList.add(errorData);
                continue;
            }

            leftLines.add(line);

        }

        //import the article first
        for (Map<String,String> line :leftLines){

            //if article sheet .do this
            String importResult = this.getInsertDataService().insertArticleData(line);
            if (StringUtils.isNotEmpty(importResult)){
                LOGGER.error("Fail to import the line:" + line + " errorMessage:" + importResult);
                ArticleImportErrorData errorData = buildImportErrorData(line,importResult,Long.valueOf(i));
                errorList.add(errorData);
                continue;
            }

            LOGGER.info("Import article success");

        }

        //当所有文章录入后再执行相关文章的关联
        for (Map<String,String> line :leftLines){

            //if article sheet .do this
            String importResult = this.getInsertDataService().insertReferenceArticle(line);
            if (StringUtils.isNotEmpty(importResult)){
                LOGGER.error("Fail to import the Reference:" + line + " errorMessage:" + importResult);
                ArticleImportErrorData errorData = buildImportErrorData(line,importResult,Long.valueOf(i));
                errorList.add(errorData);
                continue;
            }

            LOGGER.info("Import Reference success");

        }

        return errorList;
    }

    /**
     * build the import error data
     *
     * @param line
     * @param errorMessage
     * @param lineNum
     * @return
     */
    protected ArticleImportErrorData buildImportErrorData(Map<String, String> line, String errorMessage, Long lineNum){

        if (MapUtils.isNotEmpty(line) && StringUtils.isNotEmpty(errorMessage) && lineNum != null){

            ArticleImportErrorData result = new ArticleImportErrorData();
            result.setLine(line);
            result.setErrorMessage(errorMessage);
            result.setLineNum(lineNum);

            return  result;
        }

        return null;
    }

    /**
     * validate the line to check is there have some data issue.
     *
     * @param line
     * @return
     */
    protected String validateLine(Map<String, String> line){

        StringBuilder errorMessage = new StringBuilder();

        if (!line.containsKey(ArticleConstants.Excel.Title.NO)
                || StringUtils.isEmpty(line.get(ArticleConstants.Excel.Title.NO))){
            errorMessage.append("The No is empty.");
        }

        if (!line.containsKey(ArticleConstants.Excel.Title.CONTENT)
                || StringUtils.isEmpty(line.get(ArticleConstants.Excel.Title.CONTENT))){
            errorMessage.append("The Content is empty.");
        }

        if (!line.containsKey(ArticleConstants.Excel.Title.TITLE)
                || StringUtils.isEmpty(line.get(ArticleConstants.Excel.Title.TITLE))){
            errorMessage.append("The Title is empty.");
        }

        if (!line.containsKey(ArticleConstants.Excel.Title.TAG)
                || StringUtils.isEmpty(line.get(ArticleConstants.Excel.Title.TAG))){
            errorMessage.append("The Tag is empty.");
        }

        return errorMessage.toString();
    }

    public InsertDataService getInsertDataService() {
        return insertDataService;
    }

    public void setInsertDataService(InsertDataService insertDataService) {
        this.insertDataService = insertDataService;
    }
}
