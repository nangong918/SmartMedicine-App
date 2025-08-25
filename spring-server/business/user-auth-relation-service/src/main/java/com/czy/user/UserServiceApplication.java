package com.czy.user;


import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


// mybatis-plus
@MapperScan({
        // this
        "com.czy.user.mapper",
        // minio
        "com.utils.minio.mapper",
        // api.mapper
        "com.api.mapper.user.mybatis"
})
// mongodb
@EnableMongoRepositories(basePackages = {
        "com.api.mapper.user",
        "com.czy.user"
})
// es
@EnableElasticsearchRepositories(basePackages = {
        "com.api.mapper.user",
        "com.czy.user"
})
@SpringBootApplication(scanBasePackages = {
        // 扫描api模块
        "com.czy.api",
        "com.api.mapper",
        // 扫描本模块
        "com.czy.user",
        // 扫描工具类
        "com.czy.spring",
        "com.utils.common",
        "com.utils.redisson",
        "com.utils.minio",
        "com.utils.redis",
        "com.utils.rabbitmq",
}) // 扫描bean
public class UserServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UserServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
