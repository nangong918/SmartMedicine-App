package com.utils.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(scanBasePackages = {
        "com.utils.minio",
})
public class MinioApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinioApplication.class, args);
    }
}
