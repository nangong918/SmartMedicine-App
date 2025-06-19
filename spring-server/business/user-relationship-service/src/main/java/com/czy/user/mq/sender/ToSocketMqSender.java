package com.czy.user.mq.sender;


import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.MqConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.converter.base.BaseResponseConverter;
import com.czy.api.domain.dto.base.BaseResponseData;
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
public class ToSocketMqSender {

    private final RabbitTemplate rabbitJsonTemplate;
    private final BaseResponseConverter baseResponseConverter;

    // relationship service(可靠消息)要求非快速，高可靠。采用惰性队列 + 发布确认 + 接收确认 + message ttl + 消息持久化
    public void push(Message message){
        if (message == null){
            return;
        }
        // 设置确认回调
        rabbitJsonTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                messageNoAckLog(message);
                message.getData().put("cause", cause);
                // 发送到死信队列
                sendToDeathLetterQueue(message
                );
            }
        });

        // 发送消息
        rabbitJsonTemplate.convertAndSend(
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

    /**
     * 转换并发送
     * @param baseResponseData
     */
    public void push(BaseResponseData baseResponseData){
        Message message = baseResponseConverter.getMessage(baseResponseData);
        message.setType(MessageTypeTranslator.translateClean(baseResponseData.getType()));
        if (ResponseMessageType.NULL.equals(message.getType())){
            return;
        }

        push(message);
    }

    private void messageNoAckLog(Message message){
        log.error("message消息未确认，消息发送者：{}，消息接收者：{}", message.getSenderId(), message.getReceiverId());
    }

    private void sendToDeathLetterQueue(Message message){
        // 发送到死信队列
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.DEAD_LETTER_EXCHANGE,
                MqConstants.DeadLetterQueue.Routing.RELATIONSHIP_DEAD_LETTER_ROUTING,
                message);
    }


    
}
