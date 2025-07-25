package com.czy.test.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/1/4 16:40
 * 用于测试config的配置类
 */
@Setter
@Getter
@Slf4j
@Component
@ConfigurationProperties(prefix = "test.debug")
public class TestDebugConfig {
    private boolean useGatewayProxy = false;
    private String address = "127.0.0.1";

    @Override
    public String toString() {
        return "TestDebugConfig{" +
                "useGatewayProxy=" + useGatewayProxy +
                ", address='" + address + '\'' +
                '}';
    }
}
