package com.czy.post.mq.handler;


import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.post.component.PostEventManager;
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

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
//@RabbitListener(queues = MqConstants.PostQueue.POST_TO_SERVICE_QUEUE)
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = MqConstants.PostQueue.POST_TO_SERVICE_QUEUE,
                        // 持久化队列
                        durable = "true",
                        // 排他队列
                        exclusive = "false",
                        // 自动删除：通知类型队列，无需高可靠
                        autoDelete = "true",
                        arguments = {
                                // 惰性队列
                                @Argument(name = "x-queue-mode", value = "Lazy"),
                                @Argument(name = "x-message-ttl", value = MqConstants.PostQueue.message_ttl_str, type = "java.lang.Integer"),
                                @Argument(name = "x-max-length", value = MqConstants.PostQueue.max_length_str, type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
                                @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.POST_DEAD_LETTER_QUEUE)
                        }
                ),
                exchange = @Exchange(
                        value = MqConstants.Exchange.POST_EXCHANGE,
                        type = ExchangeTypes.TOPIC,
                        durable = "true"  // 持久化交换机
                ),
                key = MqConstants.PostQueue.Routing.TO_SERVICE_ROUTING
        )
)
@Component
public class PostMqHandler {

    private final PostEventManager<Message> postEventManager;

    @RabbitHandler
    public void handleMessage(@Valid Message message){
        // 通知类型消息没有确认机制，直接处理事件
        postEventManager.process(message);
    }

}
