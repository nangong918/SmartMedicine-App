package com.utils.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13225
 * @date 2025/4/4 11:00
 */

@SpringBootApplication(scanBasePackages = {
        "com.czy.springUtils",
        "com.czy.api",
        "com.utils.mvc"
})
public class SpringMvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMvcApplication.class, args);
    }
}
