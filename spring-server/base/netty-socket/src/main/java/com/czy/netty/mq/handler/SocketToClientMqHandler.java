package com.czy.netty.mq.handler;

import com.czy.api.constant.netty.MqConstants;
import com.czy.api.domain.entity.event.Message;
import com.czy.netty.component.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/6/18 18:28
 */
@Slf4j
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        // todo 改成接收全部返回消息
                        name = MqConstants.DeadLetterQueue.ALL_DEAD_LETTER_QUEUE
                ),
                exchange = @Exchange(
                        value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE,
                        type = ExchangeTypes.TOPIC
                ),
                key = MqConstants.DeadLetterQueue.Routing.ALL_DEAD_LETTER_ROUTING
        )
)
@RequiredArgsConstructor
@Component
public class SocketToClientMqHandler {

    private final MessageSender messageSender;

    @RabbitHandler
    public void handleMessage(@Valid Message message) {
        // 监听到消息校验之后就发送
        messageSender.pushToClient(message);
    }

}
