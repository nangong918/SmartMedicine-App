package com.czy.netty.kafka;

import com.czy.api.constant.netty.KafkaConstant;
import com.czy.api.domain.entity.event.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author 13225
 * @date 2025/6/20 16:18
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaSender {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void send(@NonNull Message message){
        String topic = Optional.ofNullable(message.getData())
                .filter(data -> data.containsKey(KafkaConstant.KAFKA_TOPIC))
                .map(data -> data.get(KafkaConstant.KAFKA_TOPIC))
                .orElse(null);

        if (!StringUtils.hasText(topic)){
            log.warn("发送的kafka消息不存在topic");
            return;
        }

        kafkaTemplate.send(topic, message);
    }
}
