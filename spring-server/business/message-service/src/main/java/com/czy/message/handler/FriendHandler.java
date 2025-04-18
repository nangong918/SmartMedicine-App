package com.czy.message.handler;


import com.czy.api.api.user.LoginService;
import com.czy.api.api.relationship.UserRelationshipService;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.netty.MessageTypeTranslator;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.domain.ao.relationship.AddUserAo;
import com.czy.api.domain.ao.relationship.HandleAddedMeAo;
import com.czy.api.domain.dto.http.response.AddUserToTargetUserResponse;
import com.czy.api.domain.dto.socket.request.AddUserRequest;
import com.czy.api.domain.dto.socket.request.DeleteUserRequest;
import com.czy.api.domain.dto.socket.request.HandleAddedUserRequest;
import com.czy.api.domain.dto.socket.response.DeleteUserResponse;
import com.czy.api.domain.entity.event.Message;
import com.czy.message.annotation.HandlerType;
import com.czy.message.component.RabbitMqSender;
import com.czy.message.handler.api.FriendApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    private final RabbitMqSender rabbitMqSender;

    @Override
    public void addUser(AddUserRequest request) {
        // TODO 入参检查
        if (request == null || !StringUtils.hasText(request.getAddUserAccount())){
            return;
        }
        // 检查账号是否已存在
        if (userService.checkAccountExist(request.getAddUserAccount()) <= 0) {
            String warningMessage = String.format("用户account不存在，account: %s", request.getAddUserAccount());
            log.warn(warningMessage);
            return;
        }

        // 转为响应体
        AddUserToTargetUserResponse response = new AddUserToTargetUserResponse();
        response.setType(MessageTypeTranslator.translateClean(request.getType()));
        response.setReceiverId(request.getSenderId());
        response.setSenderId(request.getReceiverId());
        response.setAppliedUserAccount(request.getAddUserAccount());
        response.setAppliedUserName(request.getMyName());
        response.setAppliedUserAddContent(request.getAddContent());
        response.AppliedUserApplyStatus = request.applyType;

        // 构建Message
        Message msg2 = response.getToMessage();
        // 推送消息
        rabbitMqSender.push(msg2);

        // 添加到MySQL持久化
        AddUserAo addUserAo = new AddUserAo();
        addUserAo.applyAccount = request.getSenderId();
        addUserAo.handlerAccount = request.getReceiverId();
        addUserAo.applyTime = Long.valueOf(request.getTimestamp());
        addUserAo.applyContent = request.addContent;
        addUserAo.source = request.source;
        addUserAo.applyStatus = request.applyType;

        // 未申请 (可能取消申请了)
        if (ApplyStatusEnum.NOT_APPLY.code == request.applyType){
            userRelationshipService.updateApplyStatus(addUserAo);
        }
        // 申请中 (添加好友)
        else if (ApplyStatusEnum.APPLYING.code == request.applyType){
            // 添加好友
            userRelationshipService.addUserFriend(addUserAo);
        }
        // 已处理
        else if (ApplyStatusEnum.HANDLED.code == request.applyType){
            userRelationshipService.updateApplyStatus(addUserAo);
        }
        else if (ApplyStatusEnum.DELETED.code == request.applyType) {
            userRelationshipService.deleteApplyStatus(addUserAo);
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest request) {
        // 删除消息持久化到MySQL
        AddUserAo addUserAo = new AddUserAo();
        addUserAo.applyAccount = request.getSenderId();
        addUserAo.handlerAccount = request.getReceiverId();
        addUserAo.applyTime = Long.valueOf(request.getTimestamp());
        addUserAo.applyStatus = request.applyType;

        userRelationshipService.deleteFriend(addUserAo);

        // Netty响应
        DeleteUserResponse response = new DeleteUserResponse();
        response.setType(MessageTypeTranslator.translateClean(request.getType()));
        response.setReceiverId(request.getSenderId());
        response.setSenderId(request.getReceiverId());
//        response.setApplyStatus(ApplyStatusEnum.DELETED.code);
        Message respMsg = response.getMessageByResponse();

        // 推送
        rabbitMqSender.push(respMsg);
    }

    @Override
    public void handleAddedUser(HandleAddedUserRequest request) {
        // Ao -> Service
        HandleAddedMeAo handleAddedMeAo = new HandleAddedMeAo();
        handleAddedMeAo.setByRequest(request);

        // 内部包含消息推送，以后进行重构
        com.czy.api.domain.entity.event.Message msg = userRelationshipService.handleAddedUser(handleAddedMeAo);
        log.info("处理加好友的响应: {}", (msg != null));

        // 推送消息
        rabbitMqSender.push(msg);
    }
}
