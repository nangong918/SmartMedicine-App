package service;

import com.czy.gateway.GatewayApplication;
import com.czy.gateway.service.FlowLimitService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = GatewayApplication.class)
@TestPropertySource("classpath:application.yml")
public class ServiceTests {

    @Test
    public void test() {
        System.out.println("hello world");
    }

    @Autowired
    private FlowLimitService flowLimitService;

    // limit Test
    @Test
    public void testLimit() {
        // 测试限流
        for(int i = 0; i < 10; i++){
            Mono<Boolean> result = flowLimitService.accessAndRecord("flowLimit_test","limit");
            result.subscribe(res -> {
                System.out.println("是否允许访问：" + res);
            });
            flowLimitService.setWhiteAndBlackList("flowLimit_test", 5, 60L);
        }
    }

}
