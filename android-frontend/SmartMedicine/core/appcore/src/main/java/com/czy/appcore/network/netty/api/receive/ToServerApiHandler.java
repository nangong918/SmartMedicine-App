package com.czy.appcore.network.netty.api.receive;

import androidx.annotation.NonNull;

import com.czy.domain.annotation.MessageType;
import com.czy.domain.constant.netty.ResponseMessageType;
import com.czy.domain.dto.netty.response.HaveReadMessageResponse;
import com.czy.domain.netty.Message;

public interface ToServerApiHandler {
    @MessageType(value = ResponseMessageType.ToServer.PONG, desc = "心跳响应")
    void pong(@NonNull Message response);

    @MessageType(value = ResponseMessageType.Chat.MESSAGE_HAVE_BEEN_READ, desc = "消息已读")
    void messageHadBeenRead(@NonNull HaveReadMessageResponse response);
}
