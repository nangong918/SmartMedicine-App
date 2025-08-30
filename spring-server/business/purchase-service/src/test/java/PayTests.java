import com.czy.purchase.PurchaseServiceApplication;
import com.czy.purchase.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * @author 13225
 * @date 2025/8/26 15:31
 */
@Slf4j
@SpringBootTest(classes = PurchaseServiceApplication.class)
@TestPropertySource("classpath:application.yml")
public class PayTests {

    @Autowired
    private OrderService orderService;

    @Test
    public void startTest(){
        log.info("start test; orderService: {}", orderService);
    }

//    @Test // 成功
//    public void createOrderTest(){
//        AppointmentOrderDto dto = new AppointmentOrderDto();
//        dto.setDoctorMerchantAppointmentId(1L);
//        dto.setUserId(1L);
//        dto.setOrderId(1L);
//        dto.setOrderStatusEnum(UserOrderStatusEnum.WAITING_PAYMENT);
//        // 设置3秒之后就过期
//        dto.setEffectiveTime(3L);
//        dto.setCurrentTime(System.currentTimeMillis());
//
//        orderService.createOrder(dto);
//
//        // 线程等待30秒
//        try {
//            Thread.sleep(30 * 1000L);
//        } catch (Exception e){
//            log.error("线程休眠失败", e);
//        }
//    }

}
