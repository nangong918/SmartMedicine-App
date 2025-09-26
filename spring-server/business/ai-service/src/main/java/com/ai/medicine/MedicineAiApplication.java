package com.ai.medicine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author 13225
 * @date 2025/9/19 16:50
 */
@SpringBootApplication
@EnableCaching
public class MedicineAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicineAiApplication.class, args);
    }

}
