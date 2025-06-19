/*
package com.czy.netty.config;

import com.czy.api.constant.mq.SocketMessageMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

*/
/**
 * @author 13225
 * @date 2025/3/31 18:16
 * RabbitMq将消息发送给多个消费者会被其中一个消费，其他无感知。
 * 如果要一条消息发送给所有消费者就需要发布订阅模式。
 * 关于RabbitMqListener使用之后是否还需要ApplicationEventListener：
 *  仍然需要使用，RabbitMq在服务级别，通知给服务。
 *  服务收到消息继续通知给各个组件就用ApplicationEventListener
 *//*


@Configuration
public class RabbitMqConfig {

    // 创建死信交换机
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(SocketMessageMqConstant.DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
    }

    // 创建死信队列
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(SocketMessageMqConstant.DEAD_LETTER_QUEUE)
                .build();
    }

    // 绑定死信队列到交换机
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(SocketMessageMqConstant.DEAD_LETTER_QUEUE);
    }


    // 创建用户发送队列
    @Bean
    public Queue userSendQueue() {
//        return QueueBuilder.durable(SocketMessageMqConstant.USER_SEND_QUEUE)
//                .withArgument("x-dead-letter-exchange", SocketMessageMqConstant.DEAD_LETTER_EXCHANGE)
//                .withArgument("x-dead-letter-routing-key", SocketMessageMqConstant.DEAD_LETTER_QUEUE)
//                .build();
        return new Queue(SocketMessageMqConstant.USER_SEND_QUEUE, // Queue 名字
                true, // durable: 是否持久化
                false, // exclusive: 是否排它
                false); // autoDelete: 是否自动删除
    }
}
*/
