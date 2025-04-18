package com.czy.api.constant.mq;

/**
 * @author 13225
 * @date 2025/3/31 18:04
 */
public class PostMqConstant {



    // service消息
    // service发送消息：service -> oss（service告诉oss需要处理什么）
    public static final String SERVICE_TO_OSS_QUEUE = "post.to.oss.queue";

    // 死信队列
    // 死信队列名称
    public static final String DEAD_LETTER_QUEUE = "oss.dead.letter.queue";
    // 为每个队列定义死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "oss.dead.letter.exchange";
}
