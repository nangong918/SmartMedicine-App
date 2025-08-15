package com.czy.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(scanBasePackages = {
        "com.utils.minio",
        "com.utils.redisson",
        "com.czy.test",
        "com.czy.api",
        "com.api.mapper",
})
@EnableElasticsearchRepositories(basePackages = "com.api.mapper")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
