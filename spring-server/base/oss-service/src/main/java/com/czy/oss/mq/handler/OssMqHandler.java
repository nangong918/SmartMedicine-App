package com.czy.oss.mq.handler;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.OssTask;
import com.czy.api.domain.entity.event.event.MessageRouteEvent;
import com.czy.api.domain.entity.event.event.OssTaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
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
//@RabbitListener(queues = MqConstants.OssQueue.OSS_TO_SERVICE_QUEUE)
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = MqConstants.OssQueue.OSS_TO_SERVICE_QUEUE,
                        // 持久化队列
                        durable = "true",
                        // 排他队列
                        exclusive = "false",
                        // 自动删除：通知类型队列
                        autoDelete = "true",
                        arguments = {
                                // 惰性队列 需要RabbitMq 3.6以上
//                                @Argument(name = "x-queue-mode", value = "Lazy"),
                                @Argument(name = "x-message-ttl", value = MqConstants.OssQueue.message_ttl_str, type = "java.lang.Integer"),
                                @Argument(name = "x-max-length", value = MqConstants.OssQueue.max_length_str, type = "java.lang.Integer"),
                                // 无所谓的通知消息不需要死信队列
//                                    @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
//                                    @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.OSS_DEAD_LETTER_QUEUE)
                        }
                ),
                exchange = @Exchange(
                        value = MqConstants.Exchange.OSS_EXCHANGE,
                        type = ExchangeTypes.TOPIC,
                        durable = "true"  // 持久化交换机
                ),
                key = MqConstants.OssQueue.Routing.TO_SERVICE_ROUTING
        )
)
public class OssMqHandler {

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
