package com.czy.post.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * @author 13225
 * @date 2025/5/21 16:47
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaSender {

    private KafkaTemplate<Object, Object> kafkaTemplate;

    public SendResult<> sendSearchPost(Object message, String topic) throws ExecutionException, InterruptedException {
        return kafkaTemplate.send(topic, message).get();
    }

}
