package com.czy.test.consumer;


import com.czy.test.message.Demo01Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Demo01AConsumer {


    @KafkaListener(topics = Demo01Message.TOPIC,
            groupId = "demo01-A-consumer-group-" + Demo01Message.TOPIC)
    public void onMessage(ConsumerRecord<Integer, String> record) {
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), record);
    }

//    @KafkaListener(topics = Demo01Message.TOPIC,
//            groupId = "demo01-B-consumer-group-" + Demo01Message.TOPIC)
//    public void onMessage(ConsumerRecord<Integer, String> record) throws InterruptedException {
//        logger.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), record.partition());
//        Thread.sleep(10 * 1000L);
//        Thread.sleep(1L);
//        logger.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), record.partition());
//    }

}
