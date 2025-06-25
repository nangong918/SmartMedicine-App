package com.czy.netty.mq.handler;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.netty.component.ToClientMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/6/18 18:28
 */
@Slf4j

@RequiredArgsConstructor
@Component
public class SocketToClientMqHandler {

    private final ToClientMessageSender toClientMessageSender;

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
        // 监听到消息校验之后就发送
        sendMessage(message);
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.PostQueue.POST_TO_SOCKET_QUEUE,
                            // 持久化队列
                            durable = "true",
                            // 排他队列
                            exclusive = "false",
                            // 自动删除：通知类型队列，无需高可靠
                            autoDelete = "true",
                            arguments = {
                                    // 惰性队列 需要RabbitMq 3.6以上
//                                @Argument(name = "x-queue-mode", value = "Lazy"),
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
                    key = MqConstants.PostQueue.Routing.TO_SOCKET_ROUTING
            )
    )
    public void handlePostMessage(@Valid Message message) {
        // 监听到消息校验之后就发送
        sendMessage(message);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.RelationshipQueue.RELATIONSHIP_TO_SOCKET_QUEUE,
                            // 持久化队列
                            durable = "true",
                            // 排他队列
                            exclusive = "false",
                            // 自动删除：可靠消息队列
                            autoDelete = "false",
                            arguments = {
                                    // 惰性队列 需要RabbitMq 3.6以上
//                                @Argument(name = "x-queue-mode", value = "Lazy"),
                                    @Argument(name = "x-message-ttl", value = MqConstants.RelationshipQueue.message_ttl_str, type = "java.lang.Integer"),
                                    @Argument(name = "x-max-length", value = MqConstants.RelationshipQueue.max_length_str, type = "java.lang.Integer"),
                                    @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
                                    @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.RELATIONSHIP_DEAD_LETTER_QUEUE)
                            }
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.RELATIONSHIP_EXCHANGE,
                            type = ExchangeTypes.TOPIC,
                            durable = "true"  // 持久化交换机
                    ),
                    key = MqConstants.RelationshipQueue.Routing.TO_SOCKET_ROUTING
            )
    )
    public void handleRelationshipMessage(@Valid Message message) {
        // 监听到消息校验之后就发送
        sendMessage(message);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.OssQueue.OSS_TO_SOCKET_QUEUE,
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
                    key = MqConstants.OssQueue.Routing.TO_SOCKET_ROUTING
            )
    )
    public void handleOssMessage(@Valid Message message) {
        // 监听到消息校验之后就发送
        sendMessage(message);
    }

    private void sendMessage(Message message){
        if (message.getReceiverId() != null){
            toClientMessageSender.pushToClient(message);
        }
    }

}
