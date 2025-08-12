package com.czy.user;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@MapperScan({"com.czy.user.mapper"})    // 扫描mapper
@SpringBootApplication(scanBasePackages = {
        // 扫描api模块
        "com.czy.api",
        // 扫描本模块
        "com.czy.user",
        // 扫描工具类
        "com.utils.mvc"
}) // 扫描bean
public class UserServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UserServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
