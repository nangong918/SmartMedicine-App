package com.czy.relationship.handler.api;



import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.socket.request.AddUserRequest;
import com.czy.api.domain.dto.socket.request.DeleteUserRequest;
import com.czy.api.domain.dto.socket.request.HandleAddedUserRequest;
import com.czy.springUtils.annotation.MessageType;


/**
 * @author 13225
 * @date 2025/3/10 17:11
 */
public interface FriendApi {

    @MessageType(value = RequestMessageType.Friend.ADD_FRIEND, desc = "添加好友的请求")
    void addUser(AddUserRequest request);

    @MessageType(value = RequestMessageType.Friend.DELETE_FRIEND, desc = "删除好友的请求")
    void deleteUser(DeleteUserRequest request);

    @MessageType(value = RequestMessageType.Friend.HANDLE_ADDED_USER, desc = "处理添加好友的请求")
    void handleAddedUser(HandleAddedUserRequest request);
}
