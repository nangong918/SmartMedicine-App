package com.czy.imports;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
// mybatis-plus
@MapperScan({
        "com.czy.imports.mapper",
        "com.utils.minio.mapper",
        // api.mapper
        "com.api.mapper.medicine.mybatis",
        "com.api.mapper.post.mybatis",
        "com.api.mapper.user.mybatis",
})
// mongodb
@EnableMongoRepositories(basePackages = {"com.api.mapper", "com.czy.imports"})
// es
@EnableElasticsearchRepositories(basePackages = {"com.api.mapper", "com.czy.imports"})
@SpringBootApplication(scanBasePackages = {
        // 扫描api模块
        "com.czy.api",
        "com.api.mapper",
        // 扫描本模块
        "com.czy.imports",
        // 扫描工具类
        "com.czy.spring",
        "com.utils.common",
        "com.utils.minio",
        "com.utils.redis",
        "com.utils.redisson",
//        "com.utils.rabbitmq",
})
@EnableAspectJAutoProxy // 启用aop
public class ImportsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImportsApplication.class, args);
    }
}
