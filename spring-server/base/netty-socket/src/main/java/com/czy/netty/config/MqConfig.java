package com.czy.netty.config;

import com.czy.api.constant.netty.MqConstants;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 13225
 * @date 2025/6/13 17:32
 */
@Configuration
@EnableRabbit
public class MqConfig {

    // 交换机
    @Bean
    public TopicExchange messageExchange() {
        return ExchangeBuilder.topicExchange(MqConstants.Exchange.MESSAGE_EXCHANGE)
                .durable(true) // 持久化
                .build();
    }

    // 创建死信交换机
    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(MqConstants.DeadLetterQueue.DLX_EXCHANGE)
                .durable(true) // 持久化
                .build();
    }

    // 队列
    @Bean
    public Queue messageToUserTextQueue() {
        return new Queue(MqConstants.MessageQueue.MESSAGE_TO_USER_TEXT_QUEUE);
    }

    // 创建用户发送队列
    @Bean
    public Queue userSendQueue() {
        return QueueBuilder.durable(MqConstants.MessageQueue.MESSAGE_TO_USER_TEXT_QUEUE) // 持久化
                .withArgument("x-dead-letter-exchange", MqConstants.DeadLetterQueue.DLX_EXCHANGE) // 死信交换机
                .withArgument("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.MESSAGE_DLX_QUEUE) // 死信路由键
                .build();
    }

    // 创建死信队列
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(MqConstants.DeadLetterQueue.MESSAGE_DLX_QUEUE,
                true,
                false,
                false); // 持久化，不排它，不自动删除
    }

    // 绑定交换机与队列
    @Bean
    public Binding bindingMessageToUserTextQueue(Queue messageToUserTextQueue, TopicExchange messageExchange) {
        return BindingBuilder
                .bind(messageToUserTextQueue)
                .to(messageExchange)
                .with(MqConstants.MessageQueue.MESSAGE_TO_USER_TEXT_QUEUE);
    }

    // 绑定死信队列
    @Bean
    public Binding bindingDeadLetterQueue(Queue deadLetterQueue, TopicExchange messageExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(messageExchange)
                .with(MqConstants.DeadLetterQueue.MESSAGE_DLX_QUEUE);
    }

    // 确保配置只初始化一次
    @Bean
    public static BeanPostProcessor exchangeQueueBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
                if (bean instanceof RabbitAdmin) {
                    RabbitAdmin admin = (RabbitAdmin) bean;
                    admin.afterPropertiesSet();
                }
                return bean;
            }
        };
    }

    // RabbitAdmin 确保交换机和队列只创建一次
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    // json作为消息解析
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
