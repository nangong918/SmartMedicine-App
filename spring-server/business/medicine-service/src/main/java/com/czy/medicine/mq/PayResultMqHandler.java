package com.czy.medicine.mq;

import com.czy.api.MqConstants;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import com.czy.medicine.service.AppointmentDoctorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/28 14:11
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PayResultMqHandler {

    private final AppointmentDoctorService registerAppointmentService;

    // 支付结果队列
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.PayQueue.APPOINTMENT_PAY_RESULT_QUEUE,
                            // 持久化队列
                            durable = "true",
                            // 排他队列
                            exclusive = "false",
                            // 自动删除：消息队列，需要高可靠
                            autoDelete = "false"
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.PAY_RESULT_EXCHANGE,
                            type = ExchangeTypes.TOPIC,
                            durable = "true"  // 持久化交换机
                    ),
                    key = MqConstants.PayQueue.Routing.APPOINTMENT_PAY_RESULT_ROUTING
            )
    )
    public void handlePayResultMessage(AppointmentPayResultDto dto){
        log.info("[预约服务][支付结果消息: {}]", dto);
        // 处理支付结果
        registerAppointmentService.handlePayResultMessage(dto);
    }

}
