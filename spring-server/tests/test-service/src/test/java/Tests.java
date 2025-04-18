import com.czy.test.TestApplication;
import com.czy.test.component.ThreadTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


/**
 * @author 13225
 * @date 2025/3/29 10:08
 */
@Slf4j
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource("classpath:application.yml")
public class Tests {

    @Autowired
    ThreadTest threadTest;
    private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(2000);

    @Test
    public void test() {
        for (int i = 0; i < 10000; i++){
            scheduler.execute(() -> {
                threadTest.i1++;
                threadTest.i2++;
                threadTest.i3.incrementAndGet();
            });
        }
        scheduler.shutdown();
        // 9996
        System.out.println("i1: " + threadTest.i1);
        // 9997
        System.out.println("i2: " + threadTest.i2);
        // 10000
        System.out.println("i3: " + threadTest.i3.get());
    }

}
