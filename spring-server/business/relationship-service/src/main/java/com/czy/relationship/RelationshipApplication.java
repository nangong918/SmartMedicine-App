package com.czy.relationship;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
// 扫描mapper
@MapperScan({"com.czy.relationship.mapper"})
@SpringBootApplication(
        scanBasePackages = {
                // 扫描api模块
                "com.czy.api",
                // 扫描本模块
                "com.czy.relationship",
                // 扫描工具类
                "com.utils.mvc"
        }
)
public class RelationshipApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RelationshipApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
