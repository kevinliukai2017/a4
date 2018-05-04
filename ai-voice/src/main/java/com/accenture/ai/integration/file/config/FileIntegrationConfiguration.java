package com.accenture.ai.integration.file.config;

import com.accenture.ai.logging.LogAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

@Configuration
public class FileIntegrationConfiguration {

    private static final LogAgent LOGGER = LogAgent.getLogAgent(FileIntegrationConfiguration.class);

    private static final String readDirectoryKey = "inbound.read.path";

    @Autowired
    private Environment env;

    @Bean(name="inboundRootDirectory")
    public File inboundReadDirectory() {

        LOGGER.info("#######inboundRootDirectory#######"+getInboundRootPath());

        return makeDirectory(getInboundRootPath());
    }

    @Bean(name="inboundProcessingDirectory")
    public File inboundProcessedDirectory() {
        return makeDirectory(getInboundRootPath()+"/processing");
    }

    @Bean(name="inboundErrorDirectory")
    public File inboundFailedDirectory() {
        return makeDirectory(getInboundRootPath() + "/error");
    }

    @Bean(name="inboundArchiveDirectory")
    public File inboundOutDirectory() {
        return makeDirectory(getInboundRootPath() + "/archive");
    }

    private String getInboundRootPath(){
        return env.getProperty(readDirectoryKey, "/inbound");
    }

    private File makeDirectory(String path) {
        File file = new File(path);

        if (!file.exists()){
            file.mkdirs();
        }

        return file;
    }
}
