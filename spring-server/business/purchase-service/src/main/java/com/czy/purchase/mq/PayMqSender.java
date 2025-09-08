package com.czy.purchase.mq;

import com.czy.api.MqConstants;
import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    public void sendAppointmentPayResult(@NotNull AppointmentPayResultDto dto){
        rabbitJsonTemplate.convertAndSend(
                // 支付交换机
                MqConstants.Exchange.PAY_RESULT_EXCHANGE,
                // 支付结果路由键
                MqConstants.PayQueue.Routing.APPOINTMENT_PAY_RESULT_ROUTING,
                dto
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
