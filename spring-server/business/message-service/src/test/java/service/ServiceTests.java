package service;

import com.czy.message.MessageServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = MessageServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class ServiceTests {

    @Test
    public void test() {
        System.out.println("hello world");
    }

}
