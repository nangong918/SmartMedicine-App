package com.czy.message.handler.api;


import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.socket.request.HaveReadMessageRequest;
import com.czy.message.annotation.MessageType;

/**
 * @author 13225
 * @date 2025/3/10 17:00
 *
 * 此Api较为特殊，因为ToService可能存在根本没用逻辑的情况，比如心跳请求，连接，下线。直接就在SDK处理，不会传递到此处。
 */
public interface ToServiceApi {

//    @MessageType(value = RequestMessageType.ToServer.PING, desc = "PING心跳请求 [会重构拦截，根本就不在message-service处理，直接在sdk处理]")
//    void pong(SendTextDataRequest sendTextDataRequest);

    // 已读消息
    @MessageType(value = RequestMessageType.ToServer.READ_MESSAGE, desc = "已读消息")
    void handleReadMessage(HaveReadMessageRequest request);
}
