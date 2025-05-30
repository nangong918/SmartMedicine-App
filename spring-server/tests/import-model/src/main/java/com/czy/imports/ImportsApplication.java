package com.czy.imports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(scanBasePackages = {
        "com.czy.springUtils",
        "com.czy.imports",
        "com.czy.api",
})
public class ImportsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImportsApplication.class, args);
    }
}
