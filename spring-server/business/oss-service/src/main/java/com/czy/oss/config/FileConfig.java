package com.czy.oss.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 13225
 * @date 2025/4/2 23:02
 */
@Configuration
public class FileConfig {

    @Value("${file.upload.url}")
    private String uploadFilePath;

    @Bean
    public String uploadFilePath() {
        return uploadFilePath;
    }

}
