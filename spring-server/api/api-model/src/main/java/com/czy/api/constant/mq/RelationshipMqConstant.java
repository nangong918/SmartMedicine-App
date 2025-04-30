package com.czy.api.constant.mq;

/**
 * @author 13225
 * @date 2025/3/31 18:04
 */
public class RelationshipMqConstant {

    // relationship发送消息：relation -> message
    public static final String RELATION_SEND_MESSAGE_QUEUE = "relation.send.message.queue";

    // 死信队列
    // 死信队列名称
    public static final String DEAD_LETTER_QUEUE = "relation.dead.letter.queue";
    // 为每个队列定义死信交换机
    public static final String DEAD_LETTER_EXCHANGE = "relation.dead.letter.exchange";
}
