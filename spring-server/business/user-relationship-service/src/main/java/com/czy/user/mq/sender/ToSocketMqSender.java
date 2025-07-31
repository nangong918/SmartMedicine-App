package com.czy.user.mq.sender;


import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/1 13:59
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class ToSocketMqSender implements RabbitMqSenderInterface {

    private final RabbitTemplate confirmRabbitJsonTemplate;
//    private final BaseResponseConverter baseResponseConverter;

    // relationship service(可靠消息)要求非快速，高可靠。采用惰性队列 + 发布确认 + 接收确认 + message ttl + 消息持久化
    @Override
    public void push(Message message){
        if (message == null){
            return;
        }

        // 发送消息
        confirmRabbitJsonTemplate.convertAndSend(
                // 交换机
                MqConstants.Exchange.RELATIONSHIP_EXCHANGE,
                // 路由键
                MqConstants.RelationshipQueue.Routing.TO_SOCKET_ROUTING,
                // 消息
                message,
                // 消息持久化
                messagePostProcessor -> {
                    messagePostProcessor.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return messagePostProcessor;
                }
        );
    }

    
}
