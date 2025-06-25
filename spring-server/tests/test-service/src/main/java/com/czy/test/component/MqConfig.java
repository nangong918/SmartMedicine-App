package com.czy.test.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 13225
 * @date 2025/6/13 17:32
 * 全部的Ma相关Bean都在此创建，微服务启动的时候消息队列缺失的问题。
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

    // 确认发布template
    @Bean("confirmRabbitJsonTemplate")
    public RabbitTemplate confirmRabbitJsonTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter()); // 设置 JSON 转换器
        template.setChannelTransacted(true); // 确保事务处理
        // 设置确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送失败: {}", cause);
            }
        });
        return template;
    }

}
