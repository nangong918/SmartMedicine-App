package com.czy.user.handler;


import com.czy.api.api.user_relationship.LoginService;
import com.czy.api.api.user_relationship.UserRelationshipService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.converter.domain.relationship.AddUserConverter;
import com.czy.api.converter.domain.relationship.HandleAddUserConverter;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.relationship.AddUserAo;
import com.czy.api.domain.ao.relationship.HandleAddedMeAo;
import com.czy.api.domain.dto.http.response.AddUserToTargetUserResponse;
import com.czy.api.domain.dto.socket.request.AddUserRequest;
import com.czy.api.domain.dto.socket.request.DeleteUserRequest;
import com.czy.api.domain.dto.socket.request.HandleAddedUserRequest;
import com.czy.api.domain.dto.socket.response.DeleteUserResponse;
import com.czy.api.domain.entity.event.Message;
import com.czy.springUtils.annotation.HandlerType;
import com.czy.user.handler.api.FriendApi;
import com.czy.user.mq.sender.ToSocketMqSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/3/10 17:30
 */
@HandlerType(RequestMessageType.Friend.root)
@Slf4j
@RequiredArgsConstructor
@Component
public class FriendHandler implements FriendApi {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private LoginService loginService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserRelationshipService userRelationshipService;
    private final AddUserConverter addUserConverter;
    private final ToSocketMqSender toSocketMqSender;
    private final HandleAddUserConverter handleAddUserConverter;

    @Override
    public void addUser(AddUserRequest request) {
        if (request == null || request.getSenderId() == null || request.getReceiverId() == null){
            return;
        }
        UserDo sender = userService.getUserById(request.getSenderId());
        UserDo receiver = userService.getUserById(request.getReceiverId());
        String chatJsonList = userRelationshipService.getChatListJson(
                request.getAddContent(),
                request.getSenderId(),
                request.getReceiverId(),
                Long.parseLong(request.getTimestamp()),
                sender.getAccount(),
                receiver.getAccount()
        );
        // 转为响应体
        AddUserToTargetUserResponse response = addUserConverter.requestToResponse(request);
        response.setAppliedUserChatList(chatJsonList);

        // 构建Message
        Message msg = response.getToMessage();
        // 推送消息
        toSocketMqSender.push(msg);

        // 添加到MySQL持久化
        AddUserAo addUserAo = addUserConverter.requestToAo(request);


        if (
                // 未申请 (可能取消申请了)
                ApplyStatusEnum.NOT_APPLY.code == request.applyType ||
                // 已处理
                ApplyStatusEnum.HANDLED.code == request.applyType
        ){
            userRelationshipService.updateApplyStatus(addUserAo);
        }

        else if (
                // 申请中 (添加好友)
                ApplyStatusEnum.APPLYING.code == request.applyType
        ){
            // 添加好友
            userRelationshipService.addUserFriend(addUserAo);
        }
        else if (
                // 删除
                ApplyStatusEnum.DELETED.code == request.applyType
        ) {
            userRelationshipService.deleteApplyStatus(addUserAo);
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest request) {
        // 删除消息持久化到MySQL
        AddUserAo addUserAo = addUserConverter.deleteRequestToAo(request);

        userRelationshipService.deleteFriend(addUserAo);

        // Netty响应
        DeleteUserResponse response = addUserConverter.deleteRequestToDeleteResponse(request);
//        response.setApplyStatus(ApplyStatusEnum.DELETED.code);
        Message respMsg = response.getMessageByResponse();

        // 推送
        toSocketMqSender.push(respMsg);
    }

    @Override
    public void handleAddedUser(HandleAddedUserRequest request) {
        // Ao -> Service
        HandleAddedMeAo handleAddedMeAo = handleAddUserConverter.requestToAo(request);

        // 内部包含消息推送，以后进行重构
        Message msg = userRelationshipService.handleAddedUser(handleAddedMeAo);
        log.info("处理加好友的响应: {}", (msg != null));

        // 推送消息
        toSocketMqSender.push(msg);
    }
}
