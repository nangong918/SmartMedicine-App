import com.czy.test.TestApplication;
import com.czy.test.service.RedisAopTestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author 13225
 * @date 2025/9/1 16:33
 */
@Slf4j
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource("classpath:application.yml")
public class AopTests {

    @Autowired
    private RedisAopTestService redisAopTestService;

    @Test
    public void redisAopTest(){
        String hitResult = redisAopTestService.hitTest(1L);
        String missResult = redisAopTestService.missTest(2L);
        log.info("命中结果：{}", hitResult);
        log.info("未命中结果：{}", missResult);
    }

}
