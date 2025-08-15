package com.czy.message;


import com.utils.common.debug.DebugConfig;
import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
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
//                "com.utils.webflux.handler",
                "com.czy.spring",
                // springUtils
                "com.utils.common",
                "com.utils.redisson",
                "com.utils.minio",
                "com.utils.redis",
                "com.utils.rabbitmq",
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
