package service;


import com.czy.recommend.RecommendServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


/**
 * @author 13225
 * @date 2025/1/13 15:32
 */

@Slf4j
@SpringBootTest(classes = RecommendServiceApplication.class)
@TestPropertySource("classpath:application.properties")
public class ServiceTests {



}
