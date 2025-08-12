package com.czy.user;


import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@MapperScan({"com.czy.user.mapper", "com.utils.minio.mapper"})    // 扫描mapper
@SpringBootApplication(scanBasePackages = {
        // 扫描api模块
        "com.czy.api",
        // 扫描本模块
        "com.czy.user",
        // 扫描工具类
//        "com.utils.mvc"
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
