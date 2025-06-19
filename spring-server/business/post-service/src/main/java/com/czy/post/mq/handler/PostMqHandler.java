package com.czy.post.mq.handler;


import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.post.component.PostEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = MqConstants.PostQueue.POST_TO_SERVICE_QUEUE)
@Component
public class PostMqHandler {

    private final PostEventManager<Message> postEventManager;

    @RabbitHandler
    public void handleMessage(@Valid Message message){
        // 通知类型消息没有确认机制，直接处理事件
        postEventManager.process(message);
    }

}
