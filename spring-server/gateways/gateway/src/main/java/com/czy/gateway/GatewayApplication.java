package com.czy.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {
        // 扫描api模块
        "com.czy.api",
        // 扫描本模块
        "com.czy.gateway",
        // 扫描工具类
        "com.utils.webflux.handler",
        // springUtils
        "com.czy.springUtils"
})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
