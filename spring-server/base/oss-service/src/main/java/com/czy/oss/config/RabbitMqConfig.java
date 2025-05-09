package com.czy.oss.config;
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
}
