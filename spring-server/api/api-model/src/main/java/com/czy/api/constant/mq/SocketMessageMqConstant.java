package com.czy.api.constant.mq;

/**
 * @author 13225
 * @date 2025/3/31 18:04
 */
public class SocketMessageMqConstant {

//    // 系统级别消息
//    // 系统发送的消息：system -> user（告诉用户连接成功）
//    public static final String SYSTEM_SEND_QUEUE = "system.send.queue";
//    // 系统接收信息：user -> system（收到连接成功的消息）
//    public static final String SYSTEM_RECEIVE_QUEUE = "system.receive.queue";

    // 用户消息
    // 用户发送消息：user -> system（告诉系统用户发送了消息，系统做出处理）
    public static final String USER_SEND_QUEUE = "user.send.queue";
    // 用户接收消息：system -> user（收到系统发送的消息）
    public static final String USER_RECEIVE_QUEUE = "user.receive.queue";

    // 死信队列
    // 死信队列名称
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";
    // 为每个队列定义死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";
}
