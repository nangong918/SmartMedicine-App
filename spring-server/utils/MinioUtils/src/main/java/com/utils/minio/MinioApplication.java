package com.utils.minio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@MapperScan({"com.utils.minio.mapper"})
@SpringBootApplication(scanBasePackages = {
        "com.utils.minio",
})
public class MinioApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinioApplication.class, args);
    }
}
