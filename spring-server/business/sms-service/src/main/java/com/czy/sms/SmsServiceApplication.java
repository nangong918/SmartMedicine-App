package com.czy.sms;

import com.czy.springUtils.debug.DebugConfig;
import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@EnableConfigurationProperties(DebugConfig.class)
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {
                // 扫描api模块
                "com.czy.api",
                // 扫描本模块
                "com.czy.sms",
                // 扫描工具类
                "com.utils.webflux.handler",
                // springUtils
                "com.czy.springUtils"
        },
        // 排除
        exclude = {}
)
public class SmsServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SmsServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
