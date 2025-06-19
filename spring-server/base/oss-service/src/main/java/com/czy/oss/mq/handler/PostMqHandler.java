package com.czy.oss.mq.handler;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.OssTask;
import com.czy.api.domain.entity.event.event.MessageRouteEvent;
import com.czy.api.domain.entity.event.event.OssTaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/4/18 23:49
 */

@Slf4j
@RequiredArgsConstructor
@Component
@RabbitListener(queues = MqConstants.OssQueue.OSS_TO_SERVICE_QUEUE)
public class PostMqHandler {

    private final ApplicationContext applicationContext;

    @RabbitHandler
    public void handleMessage(@Valid OssTask ossTask) {
        applicationContext.publishEvent(new OssTaskEvent(ossTask));
    }

    @RabbitHandler
    public void handleMessage(@Valid Message message){
        applicationContext.publishEvent(new MessageRouteEvent(message));
    }

}
