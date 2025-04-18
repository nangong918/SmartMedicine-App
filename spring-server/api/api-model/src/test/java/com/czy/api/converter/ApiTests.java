package com.czy.api.converter;

import com.czy.api.ApiApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = ApiApplication.class)
@TestPropertySource("classpath:application.yml")
public class ApiTests {

    @Test
    public void test() {
        System.out.println("hello world");
    }

}
