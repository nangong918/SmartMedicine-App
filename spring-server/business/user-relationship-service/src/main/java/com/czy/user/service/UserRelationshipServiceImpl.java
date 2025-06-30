package com.czy.user.service;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserRelationshipService;
import com.czy.api.api.user_relationship.UserSearchService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.constant.user_relationship.ListAddOrDeleteStatusEnum;
import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.constant.user_relationship.newUserGroup.HandleStatusEnum;
import com.czy.api.converter.domain.relationship.NewUserItemConverter;
import com.czy.api.converter.domain.relationship.SearchFriendApplyConverter;
import com.czy.api.domain.Do.relationship.FriendApplyDo;
import com.czy.api.domain.Do.relationship.UserFriendDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.relationship.*;
import com.czy.api.domain.bo.relationship.NewUserItemBo;
import com.czy.api.domain.bo.relationship.SearchFriendApplyBo;
import com.czy.api.domain.dto.socket.response.HandleAddUserResponse;
import com.czy.api.domain.entity.ChatEntity;
import com.czy.api.domain.entity.MessageEntity;
import com.czy.api.domain.entity.UserViewEntity;
import com.czy.api.domain.entity.event.Message;
import com.czy.user.mapper.mysql.relation.FriendApplyMapper;
import com.czy.user.mapper.mysql.relation.UserFriendMapper;
import com.czy.user.mq.sender.ToSocketMqSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
    private final SearchFriendApplyConverter searchFriendApplyConverter;
    private final NewUserItemConverter newUserItemConverter;
    private final ToSocketMqSender toSocketMqSender;

    private final UserService userService;
    private final UserSearchService userSearchService;
    private final FriendApplyMapper friendApplyMapper;

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;

    private final ObjectMapper objectMapper;

//    @Autowired
//    private ClusterEventsPusher clusterEventsPusher;

    @Override
    public boolean addUserFriend(AddUserAo addUserAo) {
        // 构建 FriendApplyDo 对象
        FriendApplyDo friendApplyDo = new FriendApplyDo();
        friendApplyDo.setApplyUserId(addUserAo.getApplyId());
        friendApplyDo.setHandleUserId(addUserAo.getHandlerId());
        friendApplyDo.setApplyTime(addUserAo.getApplyTime());
        friendApplyDo.setSource(addUserAo.getSource());

        // 申请状态
        friendApplyDo.setApplyStatus(addUserAo.getApplyStatus());
        // 处理状态

        UserDo applyDo = userService.getUserById(addUserAo.getApplyId());
        UserDo handlerDo = userService.getUserById(addUserAo.getHandlerId());

        // chatList
        String chatListJson = getChatListJson(addUserAo.getApplyContent(),
                addUserAo.getApplyId(), addUserAo.getHandlerId(),
                addUserAo.getApplyTime(),
                applyDo.getAccount(), handlerDo.getAccount()
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
    public String getChatListJson(String chatContent,
                                   Long senderId, Long receiverId,
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
        UserDo applyUser = userService.getUserById(handleAddedMeAo.getApplyId());
        UserDo handlerUser = userService.getUserById(handleAddedMeAo.getHandlerId());

        // 修改申请记录
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(
                handleAddedMeAo.getApplyId(), handleAddedMeAo.getHandlerId()
        );
        if (friendApplyDo != null){
            friendApplyDo.setHandleStatus(handleAddedMeAo.getHandleType());
            friendApplyDo.setHandleTime(handleAddedMeAo.getHandleTime());
            friendApplyDo.setChatList(
                    getChatListJson(
                            handleAddedMeAo.getAdditionalContent(),
                            handleAddedMeAo.getApplyId(),
                            handleAddedMeAo.getHandlerId(),
                            handleAddedMeAo.getHandleTime(),
                            applyUser.getAccount(),
                            handlerUser.getAccount()
                    )
            );
            friendApplyMapper.updateFriendApply(friendApplyDo);
        }

        // Netty将申请结果推送到客户端
        Message responseMessage = this.sendHandleResultToApplier(
                handleAddedMeAo, ResponseMessageType.Friend.HANDLE_ADDED_USER
        );

        // 同意
        if (HandleStatusEnum.AGREE.code == handleAddedMeAo.getHandleType()) {
            if (userFriendMapper.isFriend(applyUser.getId(), handlerUser.getId()) <= 0){
                UserFriendDo userFriendDo = new UserFriendDo();
                userFriendDo.setUserId(applyUser.getId());
                userFriendDo.setFriendId(handlerUser.getId());
                userFriendDo.setAddTime(
                        handleAddedMeAo.getHandleTime() == null ?
                                System.currentTimeMillis() :
                                handleAddedMeAo.getHandleTime()
                );

                if (userFriendMapper.addUserFriend(userFriendDo) > 0){
                    log.info("添加好友成功, applyId: {}, handlerId: {}", applyUser.getId(), handlerUser.getId());
                }
                return responseMessage;
            }
            else {
                String errorMsg = String.format("sender：%s 和 receiver：%s 已经是好友",
                        applyUser.getAccount(), handlerUser.getAccount());
                log.warn(errorMsg);
                // 修改申请记录
                setIsFriend(applyUser.getId(), handlerUser.getId(), handleAddedMeAo.getHandleTime());
                throw new AppException(errorMsg);
            }
        }
        else if (HandleStatusEnum.REFUSED.code == handleAddedMeAo.getHandleType()){
            // 拒绝
            return responseMessage;
        }
        else if (HandleStatusEnum.BLACK.code == handleAddedMeAo.getHandleType()) {
            // 拉黑
            return responseMessage;
        }
        return responseMessage;
    }

    @Override
    public Message sendHandleResultToApplier(HandleAddedMeAo handleAddedMeAo, String responseType) {
        HandleAddUserResponse response = new HandleAddUserResponse();

        // 处理方发送给申请方
        Long applyId = handleAddedMeAo.getApplyId();
        Long handlerId = handleAddedMeAo.getHandlerId();

        UserDo applyUser = userService.getUserById(applyId);
        UserDo handlerUser = userService.getUserById(handlerId);

        // response set by ao
        response.setAdditionalContent(handleAddedMeAo.getAdditionalContent());
        response.setHandlerAccount(handlerUser.getAccount());


        response.setSenderId(applyId);
        response.setApplyAccount(handlerUser.getAccount());
        // 申请方接收消息
        response.setReceiverId(handlerId);

        // response set by friendApplyDo
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(
                applyId,
                handlerId
        );
        response.setApplyStatus(friendApplyDo.getApplyStatus());
        response.setHandleStatus(friendApplyDo.getHandleStatus());
        response.setBlack(friendApplyDo.isBlack());

        // response set by userDo
        response.setHandlerAvatarFileId(handlerUser.getAvatarFileId());
        response.setHandlerName(handlerUser.getUserName());

        // netty Base
        // 服务拆分，不在此处推送，此方法的调用方是message-service，让其推送
        response.setType(responseType);
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));

        // http
        response.setCode(String.valueOf(HttpStatus.OK.value()));
        response.setMessage("");
        try {
            //            clusterEventsPusher.push(msg);
            return response.getMessageByResponse();
        } catch (Exception e){
            log.error("用户 {} 处理用户 {} 的请求响应失败，类型转化异常", applyUser, handlerUser, e);
            return null;
        }
    }

    // 设置已经是好友了
    private void setIsFriend(Long applyId, Long handleId, Long handleTime){
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(applyId, handleId);
        friendApplyDo.setApplyStatus(ApplyStatusEnum.HANDLED.code);
        friendApplyDo.setHandleStatus(HandleStatusEnum.AGREE.code);
        friendApplyDo.setHandleTime(handleTime);
        friendApplyMapper.updateFriendApply(friendApplyDo);
    }

    // 删除某条申请记录
    private void deleteFriendApply(Long applyId, Long handleId){
        FriendApplyDo friendApplyDo = friendApplyMapper.getFriendApplyByUserIds(applyId, handleId);
        if (friendApplyDo != null && friendApplyDo.getId() != null){
            friendApplyMapper.deleteFriendApply(friendApplyDo.getId());
        }
    }

    // 更新某条申请记录
    private void updateFriendApply(Long applyId, Long handleId,
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
    public List<UserViewEntity> getFriendList(Long senderId) {
        return userFriendMapper.getUserFriendsViewByUserId(senderId);
    }

    @Override
    public List<NewUserItemAo> getAddMeRequestList(Long handlerId) {
        List<NewUserItemBo> applyToMeList = friendApplyMapper.getAddMeRequestList(handlerId);
        return convertBoListToAoList(applyToMeList);
    }

    @Override
    public List<NewUserItemAo> getHandleMyAddUserResponseList(Long senderId) {
        List<NewUserItemBo> applyToMeList = friendApplyMapper.getHandleMyAddUserResponseList(senderId);
        return convertBoListToAoList(applyToMeList);
    }

    private List<NewUserItemAo> convertBoListToAoList(List<NewUserItemBo> boList) {
        List<NewUserItemAo> aoList = new ArrayList<>();
        Optional.ofNullable(boList)
                .ifPresent(list -> list.forEach(bo -> {
                    NewUserItemAo ao = newUserItemConverter.boToAo(bo);
                    aoList.add(ao);
                }));
        return aoList;
    }

    @Override
    public List<MyFriendItemAo> getMyFriendList(Long senderId) {
        List<UserViewEntity> list = userFriendMapper.getUserFriendsViewByUserId(senderId);
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
    public List<SearchFriendApplyAo> searchFriend(Long applyId, String handlerAccount) {
        // 用左连接写在MySQL，因为比先查询UserList，然后再逐个查询applyAccount和handlerAccount的状态快多了
        UserDo applyUserDo = userService.getUserById(applyId);
        if (applyUserDo == null || applyUserDo.getId() == null){
            throw new AppException("申请用户存在问题：不存在");
        }
        List<SearchFriendApplyBo> boList = friendApplyMapper.fuzzySearchHandlerByApplyAccount(applyUserDo.getAccount(), handlerAccount);

        return processFriendApplyBoList(boList);
    }

    @Override
    public List<SearchFriendApplyAo> searchFriendByName(Long applyId, String handlerUserName) {
        UserDo applyUserDo = userService.getUserById(applyId);
        if (applyUserDo == null || applyUserDo.getId() == null){
            throw new AppException("申请用户存在问题：不存在");
        }
        List<UserDo> userDos = userSearchService.searchUserByIkName(handlerUserName);
        if (CollectionUtils.isEmpty(userDos)){
            return new ArrayList<>();
        }
        // limit 20
        userDos = userDos.stream()
                .limit(20)
                .collect(Collectors.toList());
        List<SearchFriendApplyBo> boAllList = new ArrayList<>();
        userDos.forEach(handleUserDo -> {
            List<SearchFriendApplyBo> boList = friendApplyMapper.getFriendApplyByUserId(applyUserDo.getId(), handleUserDo.getId());
            boAllList.addAll(boList);
        });

        return processFriendApplyBoList(boAllList);
    }

    // listBo -> listAo
    private List<SearchFriendApplyAo> processFriendApplyBoList(List<SearchFriendApplyBo> boList) {
        if (CollectionUtils.isEmpty(boList)) {
            return new ArrayList<>();
        }

        List<Long> fileIds = boList.stream()
                .map(SearchFriendApplyBo::getAvatarFileId)
                .collect(Collectors.toList());

        List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);

        for (int i = 0; i < fileUrls.size(); i++) {
            boList.get(i).setAvatarUrl(fileUrls.get(i));
        }

        return boList.stream()
                .map(searchFriendApplyConverter::boToAo)
                .collect(Collectors.toList());
    }

    @Override
    public void updateApplyStatus(AddUserAo addUserAo) {
        UserDo applyUserDo = userService.getUserById(addUserAo.getApplyId());
        UserDo handleUserDo = userService.getUserById(addUserAo.getHandlerId());
        updateFriendApply(
                applyUserDo.getId(),
                handleUserDo.getId(),
                addUserAo.getApplyContent(),
                addUserAo.getApplyTime(),
                applyUserDo.getAccount(),
                handleUserDo.getAccount()
        );
    }

    @Override
    public void deleteApplyStatus(AddUserAo addUserAo) {
        deleteFriendApply(addUserAo.getApplyId(), addUserAo.getHandlerId());
        // 如果有记录删除
        deleteFriend(addUserAo);
    }

    @Override
    public void deleteFriend(AddUserAo addUserAo) {
        // 如果存在记录就删除
        UserFriendDo userFriendDo = userFriendMapper.getUserFriend(addUserAo.getApplyId(), addUserAo.getHandlerId());
        if (userFriendDo != null){
            userFriendMapper.deleteUserFriend(userFriendDo);

//            String userAccount = userService.getUserById(userFriendDo.getUserId()).getAccount();
//            String friendAccount = userService.getUserById(userFriendDo.getFriendId()).getAccount();
            Message deleteMessage = new Message();
            deleteMessage.setType(RequestMessageType.Chat.DELETE_ALL_MESSAGE);
            deleteMessage.setSenderId(userFriendDo.getUserId());
            deleteMessage.setReceiverId(userFriendDo.getFriendId());
            // dubbo会循环调用，此处必须用mq
            toSocketMqSender.push(deleteMessage);
        }
    }

    private Long getUserId(String account){
        Long userId = userService.getIdByAccount(account);
        if (userId == null){
            String errorMsg = String.format("account：%s 不存在", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        return userId;
    }

}
