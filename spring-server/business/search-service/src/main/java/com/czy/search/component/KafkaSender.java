package com.czy.search.component;

import com.czy.api.constant.netty.KafkaConstant;
import com.czy.api.domain.entity.kafkaMessage.UserActionSearchPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/21 16:47
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaSender {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void sendSearchAction(UserActionSearchPost message){
        kafkaTemplate.send(KafkaConstant.Topic.Search, message);
    }

}
