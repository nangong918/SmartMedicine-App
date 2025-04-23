package com.czy.message.handler.api;



import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.http.request.DisconnectRequest;
import com.czy.api.domain.dto.http.request.RegisterRequest;
import com.czy.springUtils.annotation.MessageType;

/**
 * @author 13225
 * @date 2025/3/10 15:23
 */
public interface ConnectApi {

    @MessageType(value = RequestMessageType.Connect.CONNECT, desc = "用户连接")
    void connect(RegisterRequest registerRequest);

    @MessageType(value = RequestMessageType.Connect.DISCONNECT, desc = "用户断开连接")
    void disconnect(DisconnectRequest disconnectRequest);

}
