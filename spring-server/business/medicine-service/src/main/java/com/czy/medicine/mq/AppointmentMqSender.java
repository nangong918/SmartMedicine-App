package com.czy.medicine.mq;

import com.czy.api.MqConstants;
import com.czy.api.api.RabbitMqSenderInterface;
import com.czy.api.domain.ao.medicine.AppointmentDoctorAo;
import com.czy.api.domain.dto.base.BaseResponseData;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.api.domain.entity.event.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/20 17:48
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AppointmentMqSender implements RabbitMqSenderInterface {

    // 以JSON发送
    private final RabbitTemplate rabbitJsonTemplate;

    public void push(AppointmentDoctorAo appointmentDoctorAo){
        if (appointmentDoctorAo == null){
            log.error("[预约]参数错误");
            return;
        }
        log.info("[预约]开始发送消息，appointmentDoctorAo: {}", appointmentDoctorAo);
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.APPOINTMENT_EXCHANGE,
                MqConstants.AppointmentQueue.Routing.DOCTOR_MERCHANT_ROUTING,
                appointmentDoctorAo
        );
    }

    public void push(AppointmentOrderDto appointmentOrderDto){
        if (appointmentOrderDto == null){
            log.error("[支付]参数错误");
            return;
        }
        log.info("[支付]开始发送消息，appointmentOrderDto: {}", appointmentOrderDto);
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.PAY_EXCHANGE,
                MqConstants.PayQueue.Routing.APPOINTMENT_WAIT_PAY_ROUTING,
                appointmentOrderDto
        );
    }

    // 消息发送给netty，只需要发送消息给netty，不需要接收netty的消息
    @Override
    public void push(Message message) {
        log.info("发送消息给netty：{}", message);
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.APPOINTMENT_EXCHANGE,
                MqConstants.AppointmentQueue.Routing.TO_SOCKET_ROUTING,
                message
        );
    }

    @Override
    public <T extends BaseResponseData> void push(T t) {
        RabbitMqSenderInterface.super.push(t);
    }
}
