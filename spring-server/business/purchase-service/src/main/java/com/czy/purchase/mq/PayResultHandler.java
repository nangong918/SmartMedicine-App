package com.czy.purchase.mq;

import com.czy.api.MqConstants;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.purchase.service.OrderService;
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
 * @date 2025/8/26 15:09
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PayResultHandler {

    private final OrderService orderService;

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
    public void handlePayDeathMessage(AppointmentOrderDto dto){
        log.info("收到支付超时付消息：user: {} , 商户: {}, 订单: {}, 订单状态: {}, 订单有效时间: {}, 收到时间间隔: {}",
                dto.getUserId(), dto.getDoctorMerchantAppointmentId(),
                dto.getOrderId(),
                dto.getOrderStatusEnum(),
                dto.getEffectiveTime(),
                System.currentTimeMillis() - dto.getCurrentTime());

        // 处理超时未支付订单通知medicine服务
        orderService.handleOutTimeOrder(
                dto
        );
    }

}
