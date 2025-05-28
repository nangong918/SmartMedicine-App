package com.czy.appcore.network.netty.api.receive;

import androidx.annotation.NonNull;

import com.czy.dal.annotation.MessageType;
import com.czy.dal.constant.netty.ResponseMessageType;
import com.czy.dal.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.dal.dto.netty.forwardMessage.UserImageResponse;
import com.czy.dal.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.dal.dto.netty.response.HaveReadMessageResponse;


public interface ReceiveMessageApi {

//    @MessageType(value = ResponseMessageType.Connect.CONNECT_SUCCESS, desc = "用户连接")
//    void connect(@NonNull ConnectResponse response);
//    @MessageType(value = ResponseMessageType.Connect.DISCONNECT_SUCCESS, desc = "用户断开连接")
//    void disconnect(@NonNull DisconnectResponse response);

    @MessageType(value = ResponseMessageType.Chat.RECEIVE_USER_TEXT_MESSAGE, desc = "接收用户发送资源消息")
    void receiveUserText(@NonNull UserTextDataResponse response);

    @MessageType(value = ResponseMessageType.Chat.RECEIVE_GROUP_TEXT_MESSAGE, desc = "接收群组发送文本消息")
    void receiveGroupText(@NonNull GroupTextDataResponse response);

    @MessageType(value = ResponseMessageType.Chat.MESSAGE_HAVE_BEEN_READ, desc = "标记消息已读")
    void haveReadMessage(@NonNull HaveReadMessageResponse response);

    @MessageType(value = ResponseMessageType.Chat.RECEIVE_USER_IMAGE_MESSAGE, desc = "接收用户发送图片消息")
    void receiveUserImage(@NonNull UserImageResponse response);
}
