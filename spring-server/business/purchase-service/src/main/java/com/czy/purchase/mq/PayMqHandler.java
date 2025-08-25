package com.czy.purchase.mq;

import com.czy.api.MqConstants;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/25 18:08
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PayMqHandler {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.PayQueue.APPOINTMENT_PAY_QUEUE,
                            // 持久化队列
                            durable = "true",
                            // 排他队列
                            exclusive = "false",
                            // 自动删除：消息队列，需要高可靠
                            autoDelete = "false",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
                                    @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.Routing.APPOINTMENT_PAY_DEAD_LETTER_ROUTING)
                            }
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.PAY_EXCHANGE,
                            type = ExchangeTypes.TOPIC,
                            durable = "true"  // 持久化交换机
                    ),
                    key = MqConstants.PayQueue.Routing.APPOINTMENT_ORDER_ROUTING
            )
    )
    public void handleAppointmentOrder(AppointmentOrderDto dto){

    }

    // 购物支付队列, 之后开发
/*    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.PayQueue.PURCHASE_PAY_QUEUE,
                            // 持久化队列
                            durable = "true",
                            // 排他队列
                            exclusive = "false",
                            // 自动删除：消息队列，需要高可靠
                            autoDelete = "false",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
                                    @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.Routing.PURCHASE_PAY_DEAD_LETTER_ROUTING)
                            }
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.PAY_EXCHANGE,
                            type = ExchangeTypes.TOPIC,
                            durable = "true"  // 持久化交换机
                    ),
                    key = MqConstants.PayQueue.Routing.PURCHASE_ORDER_ROUTING
            )
    )
    public void handlePurchaseOrder(){

    }*/

}
