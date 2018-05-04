package com.accenture.ai.integration.file;

import com.accenture.ai.integration.file.filter.LastModifiedFileFilter;
import com.accenture.ai.integration.file.processor.FileProcessorTask;
import com.accenture.ai.integration.file.transformer.ExcelFileToListTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileIntegrationFlow {

    @Autowired
    private File inboundRootDirectory;

    @Bean
    public IntegrationFlow processFileFlow() {
        return IntegrationFlows
                .from("fileInputChannel")
                .transform(fileToStringTransformer())
                .handle("fileProcessor", "process").get();
    }

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        CompositeFileListFilter<File> filters =new CompositeFileListFilter<>();
        filters.addFilter(new SimplePatternFileListFilter("*.xlsx"));
        filters.addFilter(new LastModifiedFileFilter());

        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setAutoCreateDirectory(true);
        source.setDirectory(inboundRootDirectory);
        source.setFilter(filters);

        return source;
    }

    @Bean
    public ExcelFileToListTransformer fileToStringTransformer() {
        return new ExcelFileToListTransformer();
    }

    @Bean
    public FileProcessorTask fileProcessor() {
        return new FileProcessorTask();
    }
}
