package com.czy.api.constant.mq;

/**
 * @author 13225
 * @date 2025/3/31 18:04
 */
public class OssMqConstant {



    // oss消息
    // oss发送消息：oss -> service（oss的响应）
    public static final String OSS_TO_SERVICE_QUEUE = "oss.to.service.queue";

    // 死信队列
    // 死信队列名称
    public static final String DEAD_LETTER_QUEUE = "oss.to.service.dead.letter.queue";
    // 为每个队列定义死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "oss.to.service.dead.letter.exchange";
}
