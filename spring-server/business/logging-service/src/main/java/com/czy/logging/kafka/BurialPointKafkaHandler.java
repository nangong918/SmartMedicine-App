package com.czy.logging.kafka;

import com.czy.api.constant.netty.KafkaConstant;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.event.BigDataEvent;
import com.czy.logging.component.LoggingEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/5/23 16:47
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class BurialPointKafkaHandler {

    private final ApplicationContext applicationContext;
    private final LoggingEventManager<Message> eventManager;

    @KafkaListener(topics = KafkaConstant.Topic.Point,
            groupId = KafkaConstant.GroupId.Feature_Implicit + KafkaConstant.Topic.Point)
    public void handleMessage(@Valid Message loggingMessage) {

        // 交给大数据logging-service
        applicationContext.publishEvent(new BigDataEvent(loggingMessage));
    }

}
