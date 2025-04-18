package com.czy.netty.component;

import com.czy.api.constant.mq.SocketMessageMqConstant;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/1 13:59
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitMqSender {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到消息服务（message-service等）
     * @param message   消息
     */
    public void sendToMessageService(Message message){
        if (message == null){
            return;
        }
        rabbitTemplate.convertAndSend(
                SocketMessageMqConstant.USER_SEND_QUEUE,
                message);
    }
    
}
