package com.czy.netty;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {"com.czy.netty",
                "com.czy.springUtils", "com.czy.api"},
        // 排除
        exclude = {}
)
public class NettySocketApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(NettySocketApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
