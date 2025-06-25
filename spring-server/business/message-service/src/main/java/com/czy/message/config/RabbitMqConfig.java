package com.czy.message.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 13225
 * @date 2025/3/31 18:16
 * RabbitMq将消息发送给多个消费者会被其中一个消费，其他无感知。
 * 如果要一条消息发送给所有消费者就需要发布订阅模式。
 * 关于RabbitMqListener使用之后是否还需要ApplicationEventListener：
 *  仍然需要使用，RabbitMq在服务级别，通知给服务。
 *  服务收到消息继续通知给各个组件就用ApplicationEventListener
 */

@Configuration
public class RabbitMqConfig {

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
        template.setChannelTransacted(true); // 确保事务处理
        return template;
    }
}
