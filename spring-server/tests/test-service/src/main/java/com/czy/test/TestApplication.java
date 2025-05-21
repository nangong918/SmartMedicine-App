package com.czy.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(scanBasePackages = {
        "com.czy.springUtils",
        "com.czy.test"
})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
