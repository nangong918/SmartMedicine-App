package mq;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.test.TestApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@SpringBootTest(classes = TestApplication.class)
public class RabbitMqTest {

    @Autowired
    private RabbitTemplate rabbitJsonTemplate;

    // 压测handler
    @Test
    public void test() throws Exception {
        for (int i = 0; i < 100; i++){
            Message message = new Message();
            message.setSenderId(1L);
            message.setReceiverId(2L);
            message.setType("message");
            Map<String, String> map = new HashMap<>();
            map.put("message", "message" + i);
            message.setData(map);
            rabbitJsonTemplate.convertAndSend(
                    MqConstants.Exchange.MESSAGE_EXCHANGE,
                    MqConstants.MessageQueue.Routing.TO_SOCKET_ROUTING,
                    message
            );
            log.info("发送消息：{}", message);
        }
        log.info("发送完毕, 等待接收者接收消息");
        Thread.sleep(1000L * 5);
    }

}
