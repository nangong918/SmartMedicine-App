package com.czy.appcore.network.netty.api.receive;

import androidx.annotation.NonNull;

import com.czy.appcore.network.netty.annotation.MessageType;
import com.czy.appcore.network.netty.constant.ResponseMessageType;
import com.czy.dal.dto.netty.response.AddUserToTargetUserResponse;
import com.czy.dal.dto.netty.response.HandleAddUserResponse;

public interface ReceiveAddUserApi {

    @MessageType(value = ResponseMessageType.Friend.ADDED_FRIEND, desc = "被添加好友")
    void receiveAddedFriend(@NonNull AddUserToTargetUserResponse response);

    @MessageType(value = ResponseMessageType.Friend.HANDLE_ADDED_USER, desc = "添加好友之后的结果")
    void receiveAddFriendResult(@NonNull HandleAddUserResponse response);

}
