package com.czy.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author 13225
 * @date 2025/5/8 17:00
 */
@Configuration
public class RestTemplateConfig {
    @Bean("restTemplate")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}