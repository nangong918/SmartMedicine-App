package com.czy.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
// 启用AOP
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = {
        "com.utils.minio",
        "com.utils.redisson",
        "com.czy.test",
        "com.czy.api",
        "com.api.mapper",
})
@EnableJpaRepositories({"com.czy.test", "com.api.mapper", "com.czy.api"})
@EnableElasticsearchRepositories(basePackages = "com.api.mapper")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
