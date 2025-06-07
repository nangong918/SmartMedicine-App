package sms;


import com.czy.api.api.auth.SmsService;
import com.czy.auth.AuthSmsServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author 13225
 * @date 2025/1/13 15:32
 */

@Slf4j
@SpringBootTest(classes = AuthSmsServiceApplication.class)
public class ServiceTests {

    @Test
    public void test() {
        log.info("test");
    }

    @Autowired
    private SmsService smsService;

    // sendSms
    @Test
    public void checkSms() {
        try {
            String phoneNumber = "12345678903";
            boolean result = smsService.sendSms(phoneNumber);
            log.info("sendSms: {}", result);
            String vcode = smsService.getVcode(phoneNumber);
            log.info("vcode: {}", vcode);
            boolean result2 = smsService.checkSms(phoneNumber, vcode);
            log.info("checkSms: {}", result2);
            smsService.checkSms(phoneNumber, "123456");
        } catch (Exception ignored){
        }
    }

}
