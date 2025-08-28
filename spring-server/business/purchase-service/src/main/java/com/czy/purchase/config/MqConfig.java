package com.czy.purchase.config;

import com.czy.api.MqConstants;
import com.czy.api.constant.purchase.PurchaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 13225
 * @date 2025/8/26 14:45
 */
@Slf4j
@Configuration
@EnableRabbit
public class MqConfig {

    /// RabbitMq配置
    // 使用Json序列化替代默认的序列化方式
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 配置Json格式消息发送
    @Bean("rabbitJsonTemplate")
    public RabbitTemplate rabbitJsonTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter()); // 设置 JSON 转换器
//        template.setChannelTransacted(true); // 确保事务处理
        return template;
    }

    /**
     * 此处注释不删除:
     * 延迟队列必须用@Bean创建, 不能用@RabbitListener创建
     * 原因: RabbitListener标注的函数会将消息消费, 导致ttl失效, 从而导致延迟功能失效
     */
    // 创建延迟队列
    @Bean
    public TopicExchange waitPayExchange() {
        return ExchangeBuilder.topicExchange(MqConstants.Exchange.PAY_EXCHANGE)
                .durable(true)
                .build();
    }

    // 声明等待队列（带TTL和死信配置）
    @Bean
    public Queue appointmentWaitPayQueue() {
        // 需要用QueueBuilder创建, 而不是new Queue
        return QueueBuilder.durable(MqConstants.PayQueue.APPOINTMENT_WAIT_PAY_QUEUE)
                // 死信队列交换机不在在@RabbitListener创建
                .deadLetterExchange(MqConstants.Exchange.PAY_RESULT_EXCHANGE)
                .deadLetterRoutingKey(MqConstants.PayQueue.Routing.APPOINTMENT_PAY_DEATH_ROUTING)
                .ttl(PurchaseConstant.MAX_WAIT_PAY_TIMEOUT)
                .build();
    }

    // 绑定等待队列到原始交换机
    @Bean
    public Binding appointmentWaitPayBinding() {
        return BindingBuilder
                .bind(appointmentWaitPayQueue())
                .to(waitPayExchange())
                .with(MqConstants.PayQueue.Routing.APPOINTMENT_WAIT_PAY_ROUTING);
    }

}
