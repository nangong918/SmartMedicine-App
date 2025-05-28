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

    // 检查密码是否合法：6-16位，包含数字、大小写字母、特殊字符、不允许有ASCII码之外的字符
    public static boolean isPasswordLegal(String password) {
        return password != null
                && password.length() >= 6
                && password.length() <= 16
                && password.matches(".*[a-z].*") // 包含小写字母
                && password.matches(".*[A-Z].*") // 包含大写字母
                && password.matches(".*\\d.*") // 包含数字
                && password.matches(".*[^a-zA-Z\\d].*") // 包含特殊字符
                && !password.matches(".*[\\s].*") // 不允许空格
                && password.matches("\\A\\p{ASCII}*\\z"); // 只包含 ASCII 字符
    }

    @Test
    public void passwordTest() {
        boolean test1 = isPasswordLegal("123456");
        boolean test2 = isPasswordLegal("123456a");
        boolean test3 = isPasswordLegal("123456a!");
        boolean test4 = isPasswordLegal("A123456a!");
        boolean test5 = isPasswordLegal("A123456a!@");
        boolean test6 = isPasswordLegal("A123456a!@#");
        boolean test7 = isPasswordLegal("123456a!@#$");
        boolean test8 = isPasswordLegal("Aa!@#$%");
        boolean test9 = isPasswordLegal("Icantse!it");
        boolean test10 = isPasswordLegal("Ican’t see it");
        boolean test11 = isPasswordLegal("A123456a!@是你吗");

        System.out.println("test1: " + test1);
        System.out.println("test2: " + test2);
        System.out.println("test3: " + test3);
        System.out.println("test4: " + test4);
        System.out.println("test5: " + test5);
        System.out.println("test6: " + test6);
        System.out.println("test7: " + test7);
        System.out.println("test8: " + test8);
        System.out.println("test9: " + test9);
        System.out.println("test10: " + test10);
        System.out.println("test11: " + test11);
    }

}
