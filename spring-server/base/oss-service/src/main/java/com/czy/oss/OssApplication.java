package com.czy.oss;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {"com.czy.oss",
                "com.czy.springUtils", "com.czy.api"},
        // 排除
        exclude = {}
)
public class OssApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OssApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
