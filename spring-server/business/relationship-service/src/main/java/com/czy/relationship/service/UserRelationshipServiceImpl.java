package com.czy.relationship.service;

import com.czy.api.api.relationship.UserRelationshipService;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.Do.relationship.FriendApplyDo;
import com.czy.api.domain.Do.relationship.UserFriendDo;
import com.czy.api.domain.ao.relationship.AddUserAo;
import com.czy.api.domain.ao.relationship.HandleAddedMeAo;
import com.czy.api.domain.ao.relationship.MyFriendItemAo;
import com.czy.api.domain.ao.relationship.NewUserItemAo;
import com.czy.api.domain.ao.relationship.SearchFriendApplyAo;
import com.czy.api.domain.bo.relationship.NewUserItemBo;
import com.czy.api.domain.bo.relationship.SearchFriendApplyBo;
import com.czy.api.domain.dto.socket.response.HandleAddUserResponse;
import com.czy.api.domain.entity.ChatEntity;
import com.czy.api.domain.entity.MessageEntity;
import com.czy.api.domain.entity.UserViewEntity;
import com.czy.api.domain.entity.event.Message;
import com.czy.relationship.constant.ApplyStatusEnum;
import com.czy.relationship.constant.HandleStatusEnum;
import com.czy.relationship.constant.ListAddOrDeleteStatusEnum;
import com.czy.relationship.mapper.FriendApplyMapper;
import com.czy.relationship.mapper.UserFriendMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/3/31 11:19
 */
@Slf4j
@RequiredArgsConstructor
@Component
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class UserRelationshipServiceImpl implements UserRelationshipService {


    private final UserFriendMapper userFriendMapper;

    // Dubbo远程调用User服务
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    private final FriendApplyMapper friendApplyMapper;

    private final ObjectMapper objectMapper;

//    @Autowired
//    private ClusterEventsPusher clusterEventsPusher;

    @Override
    public boolean addUserFriend(AddUserAo addUserAo) {
        // 获取用户id
        int senderId = getUserId(addUserAo.applyAccount);
        int receiverId = getUserId(addUserAo.handlerAccount);

        // 构建 FriendApplyDo 对象
        FriendApplyDo friendApplyDo = new FriendApplyDo();
        friendApplyDo.setApplyUserId(senderId);
        friendApplyDo.setHandleUserId(receiverId);
        friendApplyDo.setApplyTime(addUserAo.applyTime);
        friendApplyDo.setSource(addUserAo.source);

        // 申请状态
        friendApplyDo.setApplyStatus(addUserAo.applyStatus);
        // 处理状态

        // chatList
        String chatListJson = getChatListJson(addUserAo.applyContent,
                senderId, receiverId,
                addUserAo.applyTime,
                addUserAo.applyAccount, addUserAo.handlerAccount
        );
        friendApplyDo.setChatList(chatListJson);

        // 插入数据库
        int num = friendApplyMapper.insertFriendApply(friendApplyDo);
        return num > 0;
    }

    @Deprecated
    @Override
    public boolean cancelAddUserFriend(AddUserAo addUserAo) {
        // 废弃，直接在message-service改状态就好了
        return false;
    }

    // 将消息注册为ChatListJson
    private String getChatListJson(String chatContent,
                                   int senderId, int receiverId,
                                   Long timestamp,
                                   String senderAccount, String receiverAccount){
        if (!StringUtils.hasText(chatContent)){
            return null;
        }
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(senderId, receiverId);

        // 创建新的消息
        MessageEntity message = new MessageEntity();
        message.setContent(chatContent);
        // 当前时间戳
        message.setTimestamp(timestamp);

        // 创建新的 ChatEntity
        ChatEntity newChatEntity = new ChatEntity();
        newChatEntity.setMessage(message);
        newChatEntity.setSenderAccount(senderAccount);
        newChatEntity.setReceiverAccount(receiverAccount);
        newChatEntity.setTimestamp(timestamp);

        if (friendApplyDo != null && StringUtils.hasText(friendApplyDo.getChatList())){
            try {
                // 解析现有的 chatList JSON
                List<ChatEntity> chatList = objectMapper.readValue(friendApplyDo.getChatList(), objectMapper.getTypeFactory().constructCollectionType(List.class, ChatEntity.class));
                // 添加新的消息到 chatList
                chatList.add(newChatEntity);
                // 将更新后的 chatList 转换回 JSON 字符串
                return objectMapper.writeValueAsString(chatList);
            } catch (JsonProcessingException e) {
                log.error("JSON解析错误", e);
            }
        }

        List<ChatEntity> chatList = new ArrayList<>();
        chatList.add(newChatEntity);
        try {
            return objectMapper.writeValueAsString(chatList);
        } catch (JsonProcessingException e) {
            log.error("JSON写入错误", e);
        }
        return null;
    }

    @Override
    public Message handleAddedUser(HandleAddedMeAo handleAddedMeAo) {
        int applyId = getUserId(handleAddedMeAo.applyAccount);
        int handlerId = getUserId(handleAddedMeAo.handlerAccount);

        // 修改申请记录
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(applyId, handlerId);
        if (friendApplyDo != null){
            friendApplyDo.setHandleStatus(handleAddedMeAo.handleType);
            friendApplyDo.setHandleTime(handleAddedMeAo.handleTime);
            friendApplyDo.setChatList(
                    getChatListJson(
                            handleAddedMeAo.additionalContent,
                            applyId,
                            handlerId,
                            handleAddedMeAo.handleTime,
                            handleAddedMeAo.applyAccount,
                            handleAddedMeAo.handlerAccount
                    )
            );
            friendApplyMapper.updateFriendApply(friendApplyDo);
        }

        // Netty将申请结果推送到客户端
        Message responseMessage = this.sendHandleResultToApplier(handleAddedMeAo, ResponseMessageType.Friend.HANDLE_ADDED_USER);

        // 同意
        if (HandleStatusEnum.AGREE.code == handleAddedMeAo.handleType) {
            if (userFriendMapper.isFriend(applyId, handlerId) <= 0){
                UserFriendDo userFriendDo = new UserFriendDo();
                userFriendDo.setUserId(applyId);
                userFriendDo.setFriendId(handlerId);
                userFriendDo.setAddTime(handleAddedMeAo.handleTime == null ? System.currentTimeMillis() : handleAddedMeAo.handleTime);

                if (userFriendMapper.addUserFriend(userFriendDo) > 0){
                    log.info("添加好友成功, applyId: {}, handlerId: {}", applyId, handlerId);
                }
                return responseMessage;
            }
            else {
                String errorMsg = String.format("sender：%s 和 receiver：%s 已经是好友", handleAddedMeAo.applyAccount, handleAddedMeAo.handlerAccount);
                log.warn(errorMsg);
                // 修改申请记录
                setIsFriend(applyId, handlerId, handleAddedMeAo.handleTime);
                throw new AppException(errorMsg);
            }
        }
        else if (HandleStatusEnum.REFUSED.code == handleAddedMeAo.handleType){
            // 拒绝
            return responseMessage;
        }
        else if (HandleStatusEnum.BLACK.code == handleAddedMeAo.handleType) {
            // 拉黑
            return responseMessage;
        }
        return responseMessage;
    }

    @Override
    public Message sendHandleResultToApplier(HandleAddedMeAo handleAddedMeAo, String responseType) {
        HandleAddUserResponse handleAddUserResponse = new HandleAddUserResponse();

        // handleAddUserResponse
        handleAddUserResponse.additionalContent = handleAddedMeAo.additionalContent;
        UserDo userDo = userService.getUserByAccount(handleAddedMeAo.handlerAccount);
        handleAddUserResponse.avatarUrl = userDo.getAvatarUrl();
        handleAddUserResponse.userName = userDo.getUserName();
        handleAddUserResponse.userAccount = handleAddedMeAo.handlerAccount;
        Integer applierId = getUserId(handleAddedMeAo.applyAccount);
        Integer handlerId = getUserId(handleAddedMeAo.handlerAccount);

        // addUserStatusAo
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(
                applierId,
                handlerId
        );
        handleAddUserResponse.applyStatus = friendApplyDo.applyStatus;
        handleAddUserResponse.handleStatus = friendApplyDo.handleStatus;
        handleAddUserResponse.isBlack = friendApplyDo.isBlack;
        handleAddUserResponse.applyAccount = handleAddedMeAo.applyAccount;
        handleAddUserResponse.handlerAccount = handleAddedMeAo.handlerAccount;

        // netty Base
        // 服务拆分，不在此处推送，此方法的调用方是message-service，让其推送
        // 处理方发送给申请方
        handleAddUserResponse.setSenderId(handleAddedMeAo.handlerAccount);
        // 申请方接收消息
        handleAddUserResponse.setReceiverId(handleAddedMeAo.applyAccount);
        handleAddUserResponse.setType(responseType);
        handleAddUserResponse.setTimestamp(String.valueOf(System.currentTimeMillis()));

        // http
        handleAddUserResponse.setCode(String.valueOf(HttpStatus.OK.value()));
        handleAddUserResponse.setMessage("");
        try {
            //            clusterEventsPusher.push(msg);
            return handleAddUserResponse.getMessageByResponse();
        } catch (Exception e){
            log.error("用户 {} 处理用户 {} 的请求响应失败，类型转化异常",handleAddedMeAo.handlerAccount, handleAddedMeAo.applyAccount, e);
            return null;
        }
    }

    // 设置已经是好友了
    private void setIsFriend(int applyId, int handleId, Long handleTime){
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(applyId, handleId);
        friendApplyDo.setApplyStatus(ApplyStatusEnum.HANDLED.code);
        friendApplyDo.setHandleStatus(HandleStatusEnum.AGREE.code);
        friendApplyDo.setHandleTime(handleTime);
        friendApplyMapper.updateFriendApply(friendApplyDo);
    }

    // 删除某条申请记录
    private void deleteFriendApply(int applyId, int handleId){
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(applyId, handleId);
        if (friendApplyDo != null && friendApplyDo.getId() != null){
            friendApplyMapper.deleteFriendApply(friendApplyDo.getId());
        }
    }

    // 更新某条申请记录
    private void updateFriendApply(int applyId, int handleId,
                                   String additionalContent,
                                   Long timestamp,
                                   String senderAccount, String receiverAccount){
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(applyId, handleId);
        friendApplyDo.setHandleTime(timestamp);

        String chatList = getChatListJson(
                additionalContent,
                applyId, handleId,
                timestamp,
                senderAccount,
                receiverAccount
        );
        friendApplyDo.setChatList(chatList);

        friendApplyMapper.updateFriendApply(friendApplyDo);
    }

    @Override
    public List<UserViewEntity> getFriendList(String senderAccount) {
        return userFriendMapper.getUserFriendsViewByAccount(senderAccount);
    }

    @Override
    public List<NewUserItemAo> getAddMeRequestList(String handlerAccount) {
        int handlerId = getUserId(handlerAccount);
        List<NewUserItemBo> applyToMeList = friendApplyMapper.getAddMeRequestList(handlerId);
        List<NewUserItemAo> newUserItemAoList = new ArrayList<>();
        Optional.ofNullable(applyToMeList)
                .ifPresent(list -> {
                    list.forEach(bo -> {
                        NewUserItemAo newUserItemAo = new NewUserItemAo();
                        newUserItemAo.setByNewUserItemBo(bo);
                        newUserItemAoList.add(newUserItemAo);
                    });
                });
        return newUserItemAoList;
    }

    @Override
    public List<NewUserItemAo> getHandleMyAddUserResponseList(String senderAccount) {
        int senderId = getUserId(senderAccount);
        List<NewUserItemBo> applyToMeList = friendApplyMapper.getHandleMyAddUserResponseList(senderId);
        List<NewUserItemAo> newUserItemAoList = new ArrayList<>();
        Optional.ofNullable(applyToMeList)
                .ifPresent(list -> {
                    list.forEach(newUserItemBo -> {
                        NewUserItemAo newUserItemAo = new NewUserItemAo();
                        newUserItemAo.setByNewUserItemBo(newUserItemBo);
                        newUserItemAoList.add(newUserItemAo);
                    });
                });
        return newUserItemAoList;
    }

    @Override
    public List<MyFriendItemAo> getMyFriendList(String senderAccount) {
        List<UserViewEntity> list = userFriendMapper.getUserFriendsViewByAccount(senderAccount);
        List<MyFriendItemAo> myFriendItemAoList = new ArrayList<>();
        Optional.ofNullable(list)
                .ifPresent(list1 -> {
                    list1.forEach(userViewEntity -> {
                        MyFriendItemAo myFriendItemAo = new MyFriendItemAo();
                        myFriendItemAo.userViewEntity = userViewEntity;
                        myFriendItemAo.checkIsFriendStatus = ListAddOrDeleteStatusEnum.UPDATE.code;
                        myFriendItemAoList.add(myFriendItemAo);
                    });
                });
        return myFriendItemAoList;
    }

    @Override
    public List<SearchFriendApplyAo> searchFriend(String applyAccount, String handlerAccount) {
        // 用左连接写在MySQL，因为比先查询UserList，然后再逐个查询applyAccount和handlerAccount的状态快多了
        List<SearchFriendApplyBo> boList = friendApplyMapper.fuzzySearchHandlerByApplyAccount(applyAccount, handlerAccount);
        // 使用 Stream API 进行转换
        return boList.stream()
                .map(bo -> {
                    SearchFriendApplyAo ao = new SearchFriendApplyAo();
                    ao.account = bo.account;
                    ao.userName = bo.userName;
                    ao.phone = bo.phone;
                    ao.applyTime = bo.applyTime; // 这里需要修改 SearchFriendApplyAo 类以添加 applyTime
                    ao.handleTime = bo.handleTime; // 同样，这里需要修改 SearchFriendApplyAo 类以添加 handleTime
                    ao.source = bo.source; // 这里需要修改 SearchFriendApplyAo 类以添加 source
                    ao.chatList = bo.chatList; // 同样，添加 chatList
                    ao.avatarUri = bo.avatarUri;
                    // 设置 AddUserStatusAo
                    ao.addUserStatusAo.applyStatus = bo.applyStatus;
                    ao.addUserStatusAo.handleStatus = bo.handleStatus;
                    ao.addUserStatusAo.isBlack = bo.isBlack;
                    ao.addUserStatusAo.applyAccount = bo.applyAccount;
                    ao.addUserStatusAo.handlerAccount = bo.handlerAccount;
                    return ao;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateApplyStatus(AddUserAo addUserAo) {
        int applyId = getUserId(addUserAo.applyAccount);
        int handleId = getUserId(addUserAo.handlerAccount);
        updateFriendApply(
                applyId,
                handleId,
                addUserAo.applyContent,
                addUserAo.applyTime,
                addUserAo.applyAccount,
                addUserAo.handlerAccount
        );
    }

    @Override
    public void deleteApplyStatus(AddUserAo addUserAo) {
        int applyId = getUserId(addUserAo.applyAccount);
        int handleId = getUserId(addUserAo.handlerAccount);
        deleteFriendApply(applyId, handleId);
        // 如果有记录删除
        deleteFriend(addUserAo);
    }

    @Override
    public void deleteFriend(AddUserAo addUserAo) {
        int applyId = getUserId(addUserAo.applyAccount);
        int handleId = getUserId(addUserAo.handlerAccount);
        // 如果存在记录就删除
        UserFriendDo userFriendDo = userFriendMapper.getUserFriend(applyId, handleId);
        if (userFriendDo != null){
            userFriendMapper.deleteUserFriend(userFriendDo);
        }
    }

    private Integer getUserId(String account){
        Integer userId = userService.getIdByAccount(account);
        if (userId == null){
            String errorMsg = String.format("account：%s 不存在", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        return userId;
    }

}
