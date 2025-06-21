package com.czy.user.mq.handler;


import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.api.domain.entity.event.event.MessageRouteEvent;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.io.IOException;

/**
 * @author 13225
 * @date 2025/3/18 10:20
 * 事件驱动的本质是一对多，一个消息可以被多个Listener接收，是松耦合；而不是根据消息类型进行单点传递，这是强耦合。
 */

@Slf4j
@RequiredArgsConstructor
//@RabbitListener(queues = SocketMessageMqConstant.USER_RECEIVE_QUEUE)
//@RabbitListener(queues = MqConstants.RelationshipQueue.RELATIONSHIP_TO_SERVICE_QUEUE)
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = MqConstants.RelationshipQueue.RELATIONSHIP_TO_SERVICE_QUEUE,
                        // 持久化队列
                        durable = "true",
                        // 排他队列
                        exclusive = "false",
                        // 自动删除：消息队列，需要高可靠
                        autoDelete = "false",
                        arguments = {
                                // 惰性队列
                                @Argument(name = "x-queue-mode", value = "Lazy"),
                                @Argument(name = "x-message-ttl", value = MqConstants.RelationshipQueue.message_ttl_str, type = "java.lang.Integer"),
                                @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
                                @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.RELATIONSHIP_DEAD_LETTER_QUEUE)
                        }
                ),
                exchange = @Exchange(
                        value = MqConstants.Exchange.RELATIONSHIP_EXCHANGE,
                        type = ExchangeTypes.TOPIC,
                        durable = "true"  // 持久化交换机
                ),
                key = MqConstants.RelationshipQueue.Routing.TO_SERVICE_ROUTING
        )
)
@Component
public class RelationshipUserToServerMqHandler {

    private final ApplicationContext applicationContext;

    @RabbitHandler
    public void handleMessage(@Valid Message message,
                              Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        // 发送方对mq进行分工，现在无需在此确认是否是发送给本微服务的
        applicationContext.publishEvent(new MessageRouteEvent(message));
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
/**
 * 关于事件驱动架构设计：
 * 一个消息可以交给多个监听者异步处理
 * 转发推送服务，存储服务，统计服务？
 * 存储服务：每个消息的存储方式都不同，还不能直接存储，但是消息可以缓存。
 * 比如说现在一个消息会影响两个监听者？
 * 用户大数据收集监听者：用户最近活跃时间，用户活跃的频率。
 */
