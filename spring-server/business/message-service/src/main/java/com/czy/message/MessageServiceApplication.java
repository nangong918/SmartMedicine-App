package com.czy.message;

import com.czy.springUtils.debug.DebugConfig;
import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@EnableConfigurationProperties(DebugConfig.class)
// 扫描mapper
@MapperScan({"com.czy.message.mapper"})
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {
                // 扫描api模块
                "com.czy.api",
                // 扫描本模块
                "com.czy.message",
                // 扫描工具类
                "com.utils.webflux.handler",
                // springUtils
                "com.czy.springUtils"
        },
        // 排除
        exclude = {}
)
public class MessageServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MessageServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
