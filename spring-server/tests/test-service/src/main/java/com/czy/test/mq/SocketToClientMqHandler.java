package com.czy.test.mq;


import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


/**
 * @author 13225
 * @date 2025/6/18 18:28
 */
@Slf4j

@RequiredArgsConstructor
@Component
public class SocketToClientMqHandler {



    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.MessageQueue.MESSAGE_TO_SOCKET_QUEUE,
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
                    key = MqConstants.MessageQueue.Routing.TO_SOCKET_ROUTING
            )
    )
    public void handleMessageMessage(Message message) {
        log.info("收到消息：{}",  message);
    }

    
}
