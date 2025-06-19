package com.czy.netty.config;

import com.czy.api.constant.netty.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/6/13 17:32
 * 全部的Ma相关Bean都在此创建，微服务启动的时候消息队列缺失的问题。
 */
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
        template.setChannelTransacted(true); // 确保事务处理
        return template;
    }

    ///  交换机
    // 创建 Topic 交换机
    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(MqConstants.Exchange.MESSAGE_EXCHANGE);
    }

    @Bean
    public TopicExchange postExchange() {
        return new TopicExchange(MqConstants.Exchange.POST_EXCHANGE);
    }

    @Bean
    public TopicExchange relationshipExchange() {
        return new TopicExchange(MqConstants.Exchange.RELATIONSHIP_EXCHANGE);
    }

    @Bean
    public TopicExchange ossExchange() {
        return new TopicExchange(MqConstants.Exchange.OSS_EXCHANGE);
    }

    // 这是kafka的队列，不需要在rabbitMq中创建
//    @Bean
//    public TopicExchange loggingExchange() {
//        return new TopicExchange(MqConstants.Exchange.LOGGING_EXCHANGE);
//    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(MqConstants.Exchange.DEAD_LETTER_EXCHANGE);
    }

    ///  队列 + Binding

    // message
    // 创建message到 Socket 的队列
    @Bean
    public Queue messageToSocketQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", MqConstants.MessageQueue.message_ttl); // 消息过期时间 3 分钟
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.MESSAGE_DEAD_LETTER_QUEUE); // 对应的死信路由键

        return new Queue(
                MqConstants.MessageQueue.MESSAGE_TO_SOCKET_QUEUE,
                true, false, false, args);
    }
    // 绑定关系到 Socket 的队列到交换机
    @Bean
    public Binding bindingMessageToSocket() {
        return BindingBuilder.bind(messageToSocketQueue())
                .to(messageExchange())
                .with(MqConstants.MessageQueue.Routing.TO_SOCKET_ROUTING); // 绑定消息队列的路由键
    }

    // 创建message到 Service的队列
    @Bean
    public Queue messageToServiceQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", MqConstants.MessageQueue.message_ttl); // 消息过期时间 3 分钟
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.MESSAGE_DEAD_LETTER_QUEUE); // 对应的死信路由键

        return new Queue(
                MqConstants.MessageQueue.MESSAGE_TO_SERVICE_QUEUE,
                true, false, false, args);
    }
    // 绑定关系到 Service 的队列到交换机
    @Bean
    public Binding bindingMessageToService() {
        return BindingBuilder.bind(messageToServiceQueue())
                .to(messageExchange())
                .with(MqConstants.MessageQueue.Routing.TO_SERVICE_ROUTING); // 绑定消息队列的路由键
    }

    // post
    // 创建post到 Socket 的队列
    @Bean
    public Queue postToSocketQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列
        args.put("x-message-ttl", MqConstants.PostQueue.message_ttl); // 消息过期时间 10 分钟
        args.put("x-max-length", MqConstants.PostQueue.message_max_length); // 最大消息长度 10 万
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.POST_DEAD_LETTER_QUEUE); // 对应的死信路由键

        return new Queue(
                MqConstants.PostQueue.POST_TO_SOCKET_QUEUE,
                true, false, false, args);
    }
    // 绑定关系到 Socket 的队列到交换机
    @Bean
    public Binding bindingPostToSocket() {
        return BindingBuilder.bind(postToSocketQueue())
                .to(postExchange())
                .with(MqConstants.PostQueue.Routing.TO_SOCKET_ROUTING); // 绑定消息队列的路由键
    }

    // 创建post到 Service的队列
    @Bean
    public Queue postToServiceQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列
        args.put("x-message-ttl", MqConstants.PostQueue.message_ttl); // 消息过期时间 10 分钟
        args.put("x-max-length", MqConstants.PostQueue.message_max_length); // 最大消息长度 10 万
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.POST_DEAD_LETTER_QUEUE);
        return new Queue(
                MqConstants.PostQueue.POST_TO_SERVICE_QUEUE,
                true, false, false, args);
    }
    // 绑定关系到 Service 的队列到交换机
    @Bean
    public Binding bindingPostToService() {
        return BindingBuilder.bind(postToServiceQueue())
                .to(postExchange())
                .with(MqConstants.PostQueue.Routing.TO_SERVICE_ROUTING); // 绑定消息队列的路由键
    }


    // relationship
    // 创建relationship 到 Socket 的队列
    @Bean
    public Queue relationshipToSocketQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列
        args.put("x-message-ttl", MqConstants.RelationshipQueue.message_ttl); // 消息过期时间 10 分钟
        args.put("x-max-length", MqConstants.RelationshipQueue.message_max_length); // 最大消息长度 10 万
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.RELATIONSHIP_DEAD_LETTER_QUEUE); // 对应的死信路由键

        return new Queue(
                MqConstants.RelationshipQueue.RELATIONSHIP_TO_SOCKET_QUEUE,
                true, false, false, args);
    }
    // 绑定relationship 到 Socket 的队列到交换机
    @Bean
    public Binding bindingRelationshipToSocket() {
        return BindingBuilder.bind(relationshipToSocketQueue())
                .to(relationshipExchange())
                .with(MqConstants.MessageQueue.Routing.TO_SOCKET_ROUTING); // 路由键
    }

    // 创建relationship 到 service 的队列
    @Bean
    public Queue relationshipToServiceQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列
        args.put("x-message-ttl", MqConstants.RelationshipQueue.message_ttl); // 消息过期时间 10 分钟
        args.put("x-max-length", MqConstants.RelationshipQueue.message_max_length); // 最大消息长度 10 万
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.RELATIONSHIP_DEAD_LETTER_QUEUE); // 对应的死信路由键

        return new Queue(MqConstants.RelationshipQueue.RELATIONSHIP_TO_SERVICE_QUEUE,
                true, false, false, args);
    }
    // 绑定relationship到service的队列到交换机
    @Bean
    public Binding bindingRelationshipToService() {
        return BindingBuilder.bind(relationshipToServiceQueue())
                .to(relationshipExchange())
                .with(MqConstants.MessageQueue.Routing.TO_SERVICE_ROUTING); // 路由键
    }

    // oss
    // 创建oss到 Socket 的队列
    @Bean
    public Queue ossToSocketQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列
        args.put("x-message-ttl", MqConstants.OssQueue.message_ttl); // 消息过期时间 10 分钟
        args.put("x-max-length", MqConstants.OssQueue.message_max_length); // 最大消息长度 10 万
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.OSS_DEAD_LETTER_QUEUE);
        return new Queue(MqConstants.OssQueue.OSS_TO_SOCKET_QUEUE,
                true, false, false, args);
    }
    // 绑定oss到 Socket 的队列到交换机
    @Bean
    public Binding bindingOssToSocket() {
        return BindingBuilder.bind(ossToSocketQueue())
                .to(ossExchange())
                .with(MqConstants.OssQueue.Routing.TO_SOCKET_ROUTING); // 绑定消息队列的路由键
    }

    // 创建oss到 Service 的队列
    @Bean
    public Queue ossToServiceQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列
        args.put("x-message-ttl", MqConstants.OssQueue.message_ttl); // 消息过期时间 10 分钟
        args.put("x-max-length", MqConstants.OssQueue.message_max_length); // 最大消息长度 10 万
        args.put("x-dead-letter-exchange", MqConstants.Exchange.DEAD_LETTER_EXCHANGE); // 对应的死信交换机
        args.put("x-dead-letter-routing-key", MqConstants.DeadLetterQueue.OSS_DEAD_LETTER_QUEUE);
        return new Queue(MqConstants.OssQueue.OSS_TO_SERVICE_QUEUE,
                true, false, false, args);
    }
    // 绑定oss到service的队列到交换机
    @Bean
    public Binding bindingOssToService() {
        return BindingBuilder.bind(ossToServiceQueue())
                .to(ossExchange())
                .with(MqConstants.OssQueue.Routing.TO_SERVICE_ROUTING); // 绑定消息队列的路由键
    }

    ///  死信

    // message
    @Bean
    public Queue messageDeadLetterQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列

        return new Queue(MqConstants.DeadLetterQueue.MESSAGE_DEAD_LETTER_QUEUE,
                true, false, false, args);
    }

    @Bean
    public Binding bindingMessageDeadLetterQueue() {
        return BindingBuilder.bind(messageDeadLetterQueue())
                .to(deadLetterExchange())
                .with(MqConstants.DeadLetterQueue.Routing.MESSAGE_DEAD_LETTER_ROUTING); // 绑定死信队列的路由键
    }

    // relationship
    @Bean
    public Queue relationshipDeadLetterQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列

        return new Queue(MqConstants.DeadLetterQueue.RELATIONSHIP_DEAD_LETTER_QUEUE,
                true, false, false, args);
    }

    @Bean
    public Binding bindingDeadLetterQueue() {
        return BindingBuilder.bind(relationshipDeadLetterQueue())
                .to(deadLetterExchange())
                .with(MqConstants.DeadLetterQueue.Routing.RELATIONSHIP_DEAD_LETTER_ROUTING); // 绑定死信队列的路由键
    }

    // post
    @Bean
    public Queue postDeadLetterQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列

        return new Queue(MqConstants.DeadLetterQueue.POST_DEAD_LETTER_QUEUE,
                true, false, false, args);
    }

    @Bean
    public Binding bindingPostDeadLetterQueue() {
        return BindingBuilder.bind(postDeadLetterQueue())
                .to(deadLetterExchange())
                .with(MqConstants.DeadLetterQueue.Routing.POST_DEAD_LETTER_ROUTING); // 绑定死信队列的路由键
    }

    // oss
    @Bean
    public Queue ossDeadLetterQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-mode", "lazy"); // 设置为惰性队列

        return new Queue(MqConstants.DeadLetterQueue.OSS_DEAD_LETTER_QUEUE,
                true, false, false, args);
    }

    @Bean
    public Binding bindingOssDeadLetterQueue() {
        return BindingBuilder.bind(ossDeadLetterQueue())
                .to(deadLetterExchange())
                .with(MqConstants.DeadLetterQueue.Routing.OSS_DEAD_LETTER_ROUTING); // 绑定死信队列的路由键
    }

}
