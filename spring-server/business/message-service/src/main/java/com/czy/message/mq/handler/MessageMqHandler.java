package com.czy.message.mq.handler;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.message.component.MessageEventManager;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author 13225
 * @date 2025/6/19 17:08
 */
@Slf4j
@RequiredArgsConstructor
//@RabbitListener(queues = MqConstants.MessageQueue.MESSAGE_TO_SERVICE_QUEUE)
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = MqConstants.MessageQueue.MESSAGE_TO_SERVICE_QUEUE,
                        // 持久化队列
                        durable = "true",
                        // 排他队列
                        exclusive = "false",
                        // 自动删除：消息队列，需要高可靠
                        autoDelete = "false",
                        arguments = {
                                @Argument(name = "x-message-ttl", value = MqConstants.MessageQueue.message_ttl_str, type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
                                @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.MESSAGE_DEAD_LETTER_QUEUE)
                        }
                ),
                exchange = @Exchange(
                        value = MqConstants.Exchange.MESSAGE_EXCHANGE,
                        type = ExchangeTypes.TOPIC,
                        durable = "true"  // 持久化交换机
                ),
                key = MqConstants.MessageQueue.Routing.TO_SERVICE_ROUTING
        )
)
@Component
public class MessageMqHandler {

    private final MessageEventManager<Message> messageMessageEventManager;


    @RabbitHandler
    public void handleMessage(@Valid Message message,
                              Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        messageMessageEventManager.process(message);
        // 确认机制
        try {
            if (channel.isOpen()){
                channel.basicAck(deliveryTag, false);
            }
            else {
                log.warn("频道关闭，无法确认消息");
            }
        } catch (IOException e) {
            log.error("消息确认接收失败", e);
        }
    }

}
