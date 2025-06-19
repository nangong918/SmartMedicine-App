package com.czy.netty.service;

import com.czy.api.domain.entity.event.Message;

/**
 * @author 13225
 * @date 2025/6/19 11:25
 */
public interface NettyMessageService {
    /**
     * 判断netty的message应该选择哪个mq传输给service
     * @param message   netty发送给socket的消息
     * @return mq类型
     * 普配依据：
     * @see com.czy.api.constant.netty.RequestMessageType
     * 目标类型：
     * @see com.czy.api.constant.netty.MqConstants
     */
    String getNettyMessageMq(Message message);
}
