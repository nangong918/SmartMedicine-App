package com.czy.purchase.mq;

import com.czy.api.MqConstants;
import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/25 18:08
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PayMqSender implements RabbitMqSenderInterface {

    private final RabbitTemplate rabbitJsonTemplate;

    public void sendCreateOrderMessage(AppointmentOrderDto dto){
        MessagePostProcessor postProcessor = message -> {
            // 将有效时间（秒）转换为毫秒
            long ttl = dto.getEffectiveTime() * 1000;
            message.getMessageProperties().setExpiration(String.valueOf(ttl));
            return message;
        };

        dto.setCurrentTime(System.currentTimeMillis());

        rabbitJsonTemplate.convertAndSend(
                // 支付交换机
                MqConstants.Exchange.PAY_EXCHANGE,
                // 预约等待支付路由键
                MqConstants.PayQueue.Routing.APPOINTMENT_WAIT_PAY_ROUTING,
                dto,
                postProcessor
        );
    }

    @Override
    public void push(Message message) {

    }

    @Override
    public <T extends BaseResponseData> void push(T t) {
        RabbitMqSenderInterface.super.push(t);
    }
}
