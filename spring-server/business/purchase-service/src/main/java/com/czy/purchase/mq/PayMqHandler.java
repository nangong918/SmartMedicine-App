package com.czy.purchase.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 13225
 * @date 2025/8/25 18:08
 *         todo 处理  1. 创建待支付订单, 并设置待支付时间, 加入死信队列
 *          2. 支付扣减数据库 + 回调订单 + 更新medicine预约状态 + 通知前端
 *          3. 订单超时 / 订单取消 -> medicine回滚数据库库存
 */
@Slf4j
@RequiredArgsConstructor
//@Component
@Deprecated // 延迟队列不需要任何handler
public class PayMqHandler {

    /**
     * 此处注释不删除:
     * 下面方法是我之前用来实现订单超时的监听队列,
     * 但是无论是否手动确认都会造成消息被消费, 导致ttl失效
     * 经过AI说明, 发现: 此队列只能创建, 并且创建之后不能进行监听, 也就是说原罪是@RabbitListener
     * 不能使用@RabbitListener创建Queue, 要使用@Bean
     */
//    @RabbitListener(
//            bindings = @QueueBinding(
//                    value = @Queue(
//                            name = MqConstants.PayQueue.APPOINTMENT_PAY_QUEUE,
//                            // 持久化队列
//                            durable = "true",
//                            // 排他队列
//                            exclusive = "false",
//                            // 自动删除：消息队列，需要高可靠
//                            autoDelete = "false",
//                            arguments = {
//                                    @Argument(name = "x-dead-letter-exchange", value = MqConstants.Exchange.DEAD_LETTER_EXCHANGE),
//                                    @Argument(name = "x-dead-letter-routing-key", value = MqConstants.DeadLetterQueue.Routing.APPOINTMENT_PAY_DEAD_LETTER_ROUTING)
//                            }
//                    ),
//                    exchange = @Exchange(
//                            value = MqConstants.Exchange.PAY_EXCHANGE,
//                            type = ExchangeTypes.TOPIC,
//                            durable = "true"  // 持久化交换机
//                    ),
//                    key = MqConstants.PayQueue.Routing.APPOINTMENT_ORDER_ROUTING
//            )
//    )
//    public void handleAppointmentOrder(AppointmentOrderDto dto){
//
//    }

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
