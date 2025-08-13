package com.czy.imports;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@MapperScan({"com.czy.imports.mapper", "com.utils.minio.mapper"})
@SpringBootApplication(scanBasePackages = {
        "com.utils.mvc",
        "com.czy.imports",
        "com.czy.api",
        "com.utils.minio"
})
public class ImportsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImportsApplication.class, args);
    }
}
