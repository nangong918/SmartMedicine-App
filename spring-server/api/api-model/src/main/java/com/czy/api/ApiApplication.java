package com.czy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13225
 * @date 2025/4/9 21:14
 */

@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {"com.czy.api"}
)
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
