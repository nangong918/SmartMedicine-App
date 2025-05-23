package com.czy.appcore.network.netty.api.send;




import com.czy.appcore.network.netty.annotation.MessageType;
import com.czy.appcore.network.netty.constant.RequestMessageType;
import com.czy.dal.dto.netty.forwardMessage.SendImageRequest;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.dto.netty.request.DisconnectRequest;
import com.czy.dal.dto.netty.request.HandleAddedUserRequest;
import com.czy.dal.dto.netty.request.HaveReadMessageRequest;
import com.czy.dal.dto.netty.request.RegisterRequest;
import com.czy.dal.dto.netty.forwardMessage.SendTextDataRequest;

import io.reactivex.annotations.NonNull;

public interface SocketSendApi {

    @MessageType(value = RequestMessageType.Connect.CONNECT, desc = "用户连接")
    void connect(@NonNull RegisterRequest request);

    @MessageType(value = RequestMessageType.Connect.DISCONNECT, desc = "用户断开连接")
    void disconnect(@NonNull DisconnectRequest requestBody);

    @MessageType(value = RequestMessageType.Chat.SEND_TEXT_MESSAGE_TO_USER, desc = "给用户发送资源消息")
    void sendTextToUser(@NonNull SendTextDataRequest request);

    @MessageType(value = RequestMessageType.Chat.SEND_TEXT_MESSAGE_TO_GROUP, desc = "给群组发送文本消息")
    void sendTextToGroup(@NonNull SendTextDataRequest request);

    @MessageType(value = RequestMessageType.Friend.ADD_FRIEND, desc = "添加好友")
    void addFriend(@NonNull AddUserRequest request);

    @MessageType(value = RequestMessageType.Friend.HANDLE_ADDED_USER, desc = "处理添加好友请求")
    void handleAddedUser(@NonNull HandleAddedUserRequest request);

    @MessageType(value = RequestMessageType.ToServer.READ_MESSAGE, desc = "标记消息已读")
    void readMessage(@NonNull HaveReadMessageRequest request);

    @MessageType(value = RequestMessageType.Chat.SEND_IMAGE_MESSAGE_TO_USER, desc = "发送图片消息")
    void sendImageToUser(SendImageRequest request);
}
