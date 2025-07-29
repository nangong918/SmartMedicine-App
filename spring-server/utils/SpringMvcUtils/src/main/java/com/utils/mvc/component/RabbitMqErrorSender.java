package com.utils.mvc.component;

import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.MqConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/7/29 9:56
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitMqErrorSender implements RabbitMqSenderInterface {

    private final RabbitTemplate rabbitJsonTemplate;

    @Override
    public void push(Message message) {
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.ERROR_EXCHANGE,
                MqConstants.ErrorQueue.Routing.TO_SOCKET_ROUTING,
                message
        );
    }

    @Override
    public <T extends BaseResponseData> void push(T t) {
        Message message = t.getMessageByResponse();
        message.setType(MessageTypeTranslator.translateClean(t.getType()));
        if (ResponseMessageType.NULL.equals(message.getType())){
            return;
        }

        push(message);
    }
}
