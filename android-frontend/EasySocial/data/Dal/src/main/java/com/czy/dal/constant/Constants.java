package com.czy.dal.constant;


/**
 * @author 13225
 * @date 2025/2/11 23:42
 */
public interface Constants {
    // 推送消息队列
    String PUSH_MESSAGE_INNER_QUEUE = "signal/channel/PUSH_MESSAGE_INNER_QUEUE";
    // 绑定队列
    String BIND_MESSAGE_INNER_QUEUE = "signal/channel/BIND_MESSAGE_INNER_QUEUE";
    String SERVER_ID = "SERVER_ID";
    String MESSAGE_TYPE_HTTP = "messageTypeHttp";
    String CONNECTED = "connected";
    String DISCONNECTED = "disconnected";
}
