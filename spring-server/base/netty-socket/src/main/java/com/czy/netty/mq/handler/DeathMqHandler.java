package com.czy.netty.mq.handler;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/6/18 18:17
 */
@Slf4j
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = MqConstants.DeadLetterQueue.ALL_DEAD_LETTER_QUEUE,
                        // 持久化队列
                        durable = "true",
                        // 排他队列
                        exclusive = "false",
                        // 自动删除：消息队列，需要高可靠
                        autoDelete = "true",
                        arguments = {
                                // 惰性队列 需要RabbitMq 3.6以上
//                                @Argument(name = "x-queue-mode", value = "Lazy"),
                                @Argument(name = "x-message-ttl", value = MqConstants.DeadLetterQueue.message_ttl_str, type = "java.lang.Integer")
                        }
                ),
                exchange = @Exchange(
                        value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE,
                        type = ExchangeTypes.TOPIC,
                        durable = "true"  // 持久化交换机
                ),
                key = MqConstants.DeadLetterQueue.Routing.ALL_DEAD_LETTER_ROUTING
        )
)
@RequiredArgsConstructor
@Component
public class DeathMqHandler {

    @RabbitHandler
    public void handleMessage(Message message) {
        log.error("死信队列接收到消息：{}", message.toJsonString());
    }

}
