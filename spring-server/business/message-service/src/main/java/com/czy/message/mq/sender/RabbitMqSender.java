package com.czy.message.mq.sender;

import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.constant.netty.MqConstants;
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
public class RabbitMqSender implements RabbitMqSenderInterface {

//    private final RabbitTemplate confirmRabbitJsonTemplate;
    private final RabbitTemplate rabbitJsonTemplate;

    @Override
    public void push(Message message){
        if (message == null){
            return;
        }
        log.info("RabbitMqSender::发送消息：{}", message.toJsonString());
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.MESSAGE_EXCHANGE,
                MqConstants.MessageQueue.Routing.TO_SOCKET_ROUTING,
                message
        );
    }
    
}
