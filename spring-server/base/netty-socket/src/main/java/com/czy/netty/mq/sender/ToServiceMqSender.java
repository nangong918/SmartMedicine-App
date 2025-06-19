package com.czy.netty.mq.sender;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.netty.service.NettyMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author 13225
 * @date 2025/6/19 11:19
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ToServiceMqSender {

    private final NettyMessageService nettyMessageService;

    // 以JSON发送
    private final RabbitTemplate rabbitJsonTemplate;

    public void sendToService(Message message){
        if (message == null){
            return;
        }

        String mqId = nettyMessageService.getNettyMessageMq(message);
        if (StringUtils.hasText(mqId)){
            switch (mqId){
                case MqConstants.MessageQueue.ID:
                    sendToMessageService(message);
                    break;
                case MqConstants.PostQueue.ID:
                    sendToPostService(message);
                    break;
                case MqConstants.RelationshipQueue.ID:
                    sendToRelationshipService(message);
                    break;
                case MqConstants.OssQueue.ID:
                    sendToOssService(message);
                    break;
                default:
                    break;
            }
        }
        else {
            log.warn("netty发送消息给service失败，因为mqId是null");
        }
    }

    private void sendToDeathLetterQueue(Message message, String deathRouting){
        // 发送到死信队列
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.DEAD_LETTER_EXCHANGE,
                deathRouting,
                message);
    }

    private void messageNoAckLog(Message message){
        log.error("message消息未确认，消息发送者：{}，消息接收者：{}", message.getSenderId(), message.getReceiverId());
    }

    // message service(实时可靠消息)要求快速和高可靠。采用非惰性 + 发布确认 + 接收确认 + message ttl + 消息持久化
    private void sendToMessageService(Message message){
        // 设置确认回调
        rabbitJsonTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                messageNoAckLog(message);
                message.getData().put("cause", cause);
                // 发送到死信队列
                sendToDeathLetterQueue(message,
                        MqConstants.DeadLetterQueue.Routing.MESSAGE_DEAD_LETTER_ROUTING
                );
            }
        });

        // 发送消息
        rabbitJsonTemplate.convertAndSend(
                // 交换机
                MqConstants.Exchange.MESSAGE_EXCHANGE,
                // 路由键
                MqConstants.MessageQueue.Routing.TO_SERVICE_ROUTING,
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

    // post service(通知消息)要求非可靠 非快速。采用惰性队列 + message ttl + 消息持久化 + 消息队列max length
    private void sendToPostService(Message message){
        // 发送消息
        rabbitJsonTemplate.convertAndSend(
                // 交换机
                MqConstants.Exchange.POST_EXCHANGE,
                // 路由键
                MqConstants.PostQueue.Routing.TO_SERVICE_ROUTING,
                // 消息
                message
        );
    }

    // relationship service(可靠消息)要求非快速，高可靠。采用惰性队列 + 发布确认 + 接收确认 + message ttl + 消息持久化
    private void sendToRelationshipService(Message message){
        // 设置确认回调
        rabbitJsonTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                messageNoAckLog(message);
                message.getData().put("cause", cause);
                // 发送到死信队列
                sendToDeathLetterQueue(message,
                        MqConstants.DeadLetterQueue.Routing.RELATIONSHIP_DEAD_LETTER_ROUTING
                );
            }
        });

        // 发送消息
        rabbitJsonTemplate.convertAndSend(
                // 交换机
                MqConstants.Exchange.RELATIONSHIP_EXCHANGE,
                // 路由键
                MqConstants.RelationshipQueue.Routing.TO_SERVICE_ROUTING,
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

    // oss service(通知消息))要求非可靠 非快速。采用惰性队列 + message ttl + 消息持久化 + 消息队列max length
    private void sendToOssService(Message message){
        // 发送消息
        rabbitJsonTemplate.convertAndSend(
                // 交换机
                MqConstants.Exchange.OSS_EXCHANGE,
                // 路由键
                MqConstants.OssQueue.Routing.TO_SERVICE_ROUTING,
                // 消息
                message
        );
    }
}
