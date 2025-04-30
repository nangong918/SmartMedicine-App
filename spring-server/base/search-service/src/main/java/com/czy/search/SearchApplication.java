package com.czy.search;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {
                "com.czy.search",
                "com.czy.springUtils",
                "com.czy.api"
        },
        // 排除
        exclude = {}
)
public class SearchApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SearchApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
