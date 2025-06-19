package com.czy.netty.mq.handler;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.netty.component.ToClientMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
                            // 接收全部返回消息
                            name = MqConstants.MessageQueue.MESSAGE_TO_SOCKET_QUEUE
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.MESSAGE_EXCHANGE,
                            type = ExchangeTypes.TOPIC
                    ),
                    key = MqConstants.MessageQueue.Routing.TO_SOCKET_ROUTING
            )
    )
    public void handleMessageMessage(@Valid Message message) {
        // 监听到消息校验之后就发送
        sendMessage(message);
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            // 接收全部返回消息
                            name = MqConstants.PostQueue.POST_TO_SOCKET_QUEUE
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.POST_EXCHANGE,
                            type = ExchangeTypes.TOPIC
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
                            // 接收全部返回消息
                            name = MqConstants.RelationshipQueue.RELATIONSHIP_TO_SOCKET_QUEUE
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.RELATIONSHIP_EXCHANGE,
                            type = ExchangeTypes.TOPIC
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
                            // 接收全部返回消息
                            name = MqConstants.OssQueue.OSS_TO_SOCKET_QUEUE
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.RELATIONSHIP_EXCHANGE,
                            type = ExchangeTypes.TOPIC
                    ),
                    key = MqConstants.OssQueue.Routing.TO_SOCKET_ROUTING
            )
    )
    public void handleOssMessage(@Valid Message message) {
        // 监听到消息校验之后就发送
        sendMessage(message);
    }

    private void sendMessage(Message message){
        if (StringUtils.hasText(message.getReceiverId())){
            toClientMessageSender.pushToClient(message);
        }
    }

}
