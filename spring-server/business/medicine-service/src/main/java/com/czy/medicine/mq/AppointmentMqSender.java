package com.czy.medicine.mq;

import com.czy.api.MqConstants;
import com.czy.api.domain.ao.medicine.AppointmentDoctorAo;
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
public class AppointmentMqSender {

    // 以JSON发送
    private final RabbitTemplate rabbitJsonTemplate;

    public void push(AppointmentDoctorAo message){
        if (message == null){
            log.error("[预约]参数错误");
            return;
        }
        rabbitJsonTemplate.convertAndSend(
                MqConstants.Exchange.APPOINTMENT_EXCHANGE,
                MqConstants.AppointmentQueue.Routing.DOCTOR_MERCHANT_ROUTING,
                message
        );
    }

}
