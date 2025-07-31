package com.utils.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 13225
 * @date 2025/4/4 11:00
 */
@EnableAspectJAutoProxy // 启用aop
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
