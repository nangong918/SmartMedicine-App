package com.czy.message.service;


import com.czy.api.api.message.ChatService;
import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.relation.UserRelationshipService;
import com.czy.api.api.user_relationship.user.UserService;
import com.czy.api.constant.MessageTypeEnum;
import com.czy.api.constant.message.MessageConstant;
import com.czy.api.converter.domain.message.UserChatMessageConverter;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.message.FetchUserMessageAo;
import com.czy.api.domain.bo.message.UserChatLastMessageBo;
import com.czy.api.domain.bo.message.UserChatLastViewMessageBo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import com.czy.api.domain.entity.FriendViewEntity;
import com.czy.api.exception.UserExceptions;
import com.czy.message.mapper.mongo.UserChatMessageMongoMapper;
import com.czy.message.mapper.mysql.UserChatMessageMapper;
import com.czy.message.service.transactional.MessageStorageService;
import com.czy.springUtils.service.RedisService;
import exception.AppException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/2/26 17:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class ChatServiceImpl implements ChatService {

    private final UserChatMessageMapper userChatMessageMapper;
    private final RedisService redisService;
    private final MessageStorageService messageStorageService;
    private final UserChatMessageMongoMapper userChatMessageMongoMapper;
    private final UserChatMessageConverter userChatMessageConverter;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserRelationshipService userRelationshipService;

    @Override
    public List<UserChatLastViewMessageBo> getUserAllChatMessage(Long senderId) {
        List<UserChatLastMessageBo> messages = new LinkedList<>();
        // 获取所有相关的键 考虑到senderId可能是receiverId
//        Set<String> keys = redisService.getKeys(MessageConstant.CHAT_MESSAGE_KEY + senderId + ":");
        // 此时的sender是想要查询sender收到的消息；所以sender要作为receiver；所以
        // MessageConstant.CHAT_MESSAGE_KEY + bo.senderId + ":" + bo.receiverId + ":";
        // sender是receiver
        String receiverCheckKey = MessageConstant.CHAT_MESSAGE_KEY + "*:" + senderId;
        // sender是sender
        String senderCheckKey = MessageConstant.CHAT_MESSAGE_KEY + senderId + ":*";
        Set<String> keysReceiver = redisService.getKeys(receiverCheckKey);
        Set<String> keysSender = redisService.getKeys(senderCheckKey);

        // Map<senderId, bo>
        Map<Long, UserChatLastMessageBo> receiverMap = new HashMap<>();
        // user收到的消息
        for (String key : keysReceiver) {
            UserChatLastMessageBo message = redisService.getObject(key, UserChatLastMessageBo.class);
            if (message != null) {
                receiverMap.put(message.getSenderId(), message);
                messages.add(message);
            }
        }
        // user发送的消息
        // 洗数据：只添加map中（receiverId不存在的）
        for (String key : keysSender) {
            UserChatLastMessageBo message = redisService.getObject(key, UserChatLastMessageBo.class);
            // 当我发送的消息中，接受者的id没给我发消息则加入map
            if (receiverMap.get(message.getReceiverId()) == null){
                // 自己发送给别人的消息是不存在未读的
                message.setUnreadCount(0);
                messages.add(message);
            }
        }

        // 获取最近消息的friendsView
        List<UserChatLastViewMessageBo> friendsViewMessages = getViewMessageByMessage(messages, senderId);

        // 通过user的fileId为他的fileUrl赋值
        assignImageInfo(friendsViewMessages);

        // 限制返回的消息数量 (交给前端去根据时间顺序排序，节省后端算力和时间)
        return friendsViewMessages.size() > MessageConstant.MAX_RECENT_MESSAGE_COUNT ?
                friendsViewMessages.subList(0, MessageConstant.MAX_RECENT_MESSAGE_COUNT) : friendsViewMessages;
    }

    @Override
    public List<UserChatLastViewMessageBo> getViewMessageByMessage(List<UserChatLastMessageBo> boList, Long senderId) {
        if (CollectionUtils.isEmpty(boList)){
            return Collections.emptyList();
        }

        if (senderId == null){
            return Collections.emptyList();
        }

        List<Long> friendsId = new ArrayList<>();
        Map<Long, UserChatLastMessageBo> friendsBoMap = new HashMap<>();

        for (UserChatLastMessageBo bo : boList){
            // 请求者 在消息中是 发送者
            Long friendId;
            if (senderId.equals(bo.getSenderId())){
                friendId = bo.getReceiverId();
            }
            else {
                friendId = bo.getSenderId();
            }
            friendsId.add(friendId);
            friendsBoMap.put(friendId, bo);
        }

        /**        已经在数据库验证成功的sql代码
         *         SELECT
         *             CASE
         *                 WHEN uf.user_id = #{userId} THEN uf.friend_id
         *                 ELSE uf.user_id
         *             END AS friendId,
         *             CASE
         *                 WHEN uf.user_id = #{userId} THEN lfu.user_name
         *                 ELSE luu.user_name
         *             END AS friendName
         *             CASE
         *                 WHEN uf.user_id = #{userId} THEN lfu.avatar_file_id
         *                 ELSE luu.avatar_file_id
         *             END AS friendAvatarFileId
         *             CASE
         *                 WHEN uf.user_id = #{userId} THEN lfu.account
         *                 ELSE luu.account
         *             END AS friendAccount
         *             CASE
         *                 WHEN uf.user_id = #{userId} THEN uf.remark_user_for_friend
         *                 ELSE uf.remark_friend_for_user
         *             END AS remark
         *         FROM user_friend uf
         *         JOIN login_user luu ON uf.user_id = luu.id
         *         JOIN login_user luf ON uf.friend_id = luf.id
         *         WHERE
         *             uf.user_id = #{userId} OR
         *             uf.friend_id = #{userId}
         */
        // mybatis查询的结果如果无记录是不会返回到list中的，所以不能直接for循环组装
        List<FriendViewEntity> friendViewEntityList = userRelationshipService.getFriendsViewByUserIdFriendsId(
                senderId,
                friendsId
        );

        if (CollectionUtils.isEmpty(friendViewEntityList)){
            return Collections.emptyList();
        }

        // 转为Map<friendId, friendViewEntity>；避免双重for循环
        Map<Long, FriendViewEntity> friendViewEntityMap = friendViewEntityList.stream()
                .collect(Collectors.toMap(
                        // 此userId是friend的id
                        FriendViewEntity::getUserId,
                        friend -> friend)
                );

        // mybatis查询的结果如果无记录是不会返回到list中的，所以不能直接for循环组装
        List<UserChatLastViewMessageBo> userChatLastViewMessageBoList = new ArrayList<>();
        for (Long friendId : friendsId){
            if (friendId == null){
                userChatLastViewMessageBoList.add(null);
                continue;
            }

            UserChatLastMessageBo bo = friendsBoMap.get(friendId);

            FriendViewEntity friendViewEntity = friendViewEntityMap.get(friendId);
            UserChatLastViewMessageBo userChatLastViewMessageBo = new UserChatLastViewMessageBo();
            userChatLastViewMessageBo.setData(friendViewEntity, bo);
            userChatLastViewMessageBoList.add(userChatLastViewMessageBo);
        }
        return userChatLastViewMessageBoList;
    }

    @Override
    public void assignImageInfo(List<UserChatLastViewMessageBo> boList) {
        if (CollectionUtils.isEmpty(boList)){
            return;
        }
        List<Long> fileIds = new ArrayList<>();
        for (UserChatLastViewMessageBo bo : boList){
            Long avatarFileId = Optional.ofNullable(bo)
                    .map(UserChatLastViewMessageBo::getFriendViewEntity)
                    .map(FriendViewEntity::getAvatarFileId)
                    .orElse(null);
            if (bo == null || avatarFileId == null){
                fileIds.add(null);
                continue;
            }
            Long fileId = null;
            try {
                fileId = avatarFileId;
                fileIds.add(fileId);
            } catch (Exception e){
                fileIds.add(null);
            }
        }
        List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
        assert fileUrls.size() == boList.size();
        for (int i = 0; i < boList.size(); i++) {
            FriendViewEntity fe = Optional.ofNullable(boList.get(i))
                    .map(UserChatLastViewMessageBo::getFriendViewEntity)
                    .orElse(null);
            if (fe != null){
                fe.setAvatarUrl(fileUrls.get(i));
                boList.get(i).setFriendViewEntity(fe);
            }
        }
    }

    @Override
    public UserChatLastMessageBo getUserChatMessage(Long senderId, Long receiverId) {
        String key = MessageConstant.CHAT_MESSAGE_KEY + senderId + ":" + receiverId + ":";
        return redisService.getObject(key, UserChatLastMessageBo.class);
    }

    @Override
    public void clearUserChatMessageUnreadCount(Long senderId, Long receiverId) {
        String key = MessageConstant.CHAT_MESSAGE_KEY + senderId + ":" + receiverId + ":";
        UserChatLastMessageBo bo = redisService.getObject(key, UserChatLastMessageBo.class);
        if (bo != null){
            bo.setUnreadCount(0);
            redisService.setObject(key, bo, MessageConstant.CHAT_MESSAGE_EXPIRE_TIME);
        }
        else {
            log.warn("bo == null");
        }
    }

    @Override
    public List<UserChatMessageBo> getUserChatMessage(@NonNull FetchUserMessageAo fetchUserMessageAo) {
        // 参数校验
        UserDo senderDo = userService.getUserById(fetchUserMessageAo.getSenderId());
        UserDo receiverDo = userService.getUserById(fetchUserMessageAo.getReceiverId());
        if (senderDo == null || receiverDo == null || senderDo.getId() == null || receiverDo.getId() == null){
            throw new AppException(UserExceptions.USER_NOT_EXIST);
        }

        // 限制 messageCount 最大值为 200
        int messageCount = Math.min(fetchUserMessageAo.getMessageCount(), MessageConstant.MAX_SEARCH_MESSAGE_LIMIT);

        // 此处不应该是mysql查询，而应该是mongodb查询
//        return chatMapper.selectMessagesAfter(
//                senderId,
//                receiverId,
//                fetchUserMessageAo.getTimestampIndex(),
//                messageCount
//        );

        // 根据 timestampIndex 和 messageCount 查询用户聊天记录
        // 资源文件存储的是 fileIdStr
        List<UserChatMessageDo> messageDoList = userChatMessageMongoMapper.findAllMessagesAfterTimestamp(
                senderDo.getId(),
                receiverDo.getId(),
                Optional.ofNullable(fetchUserMessageAo.getTimestampIndex())
                        .orElse(System.currentTimeMillis()),
                messageCount
        );
        if (CollectionUtils.isEmpty(messageDoList)){
            return new LinkedList<>();
        }
        List<UserChatMessageDo> textMessageDoList = new ArrayList<>();
        List<UserChatMessageDo> fileMessageDoList = new ArrayList<>();
        for (UserChatMessageDo message : messageDoList) {
            if (message.getMsgType() == MessageTypeEnum.text.code) {
                textMessageDoList.add(message); // 添加到文本消息列表
            } else {
                fileMessageDoList.add(message); // 添加到非文本消息列表
            }
        }
        // 非空则将内容从id替换未url
        if (!fileMessageDoList.isEmpty()){
            List<Long> fileIds;
            try {
                fileIds = fileMessageDoList.stream()
                        .map(UserChatMessageDo::getMsgFileId)
//                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (Exception e){
                log.error("fileIdList 解析失败", e);
                throw new AppException("fileIdList 解析失败");
            }
            List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
            for (int i = 0; i < fileMessageDoList.size(); i++){
                UserChatMessageDo message = fileMessageDoList.get(i);
//                message.setMsgContent(fileUrls.get(i));
                message.setMsgFileUrl(fileUrls.get(i));
            }

            // 合并
            messageDoList.clear();
            messageDoList.addAll(fileMessageDoList);
            messageDoList.addAll(textMessageDoList);
        }
        // 排序，按时间戳降序
        messageDoList.sort(
                Comparator.comparingLong(UserChatMessageDo::getTimestamp)
                        .reversed()
        );

        return messageDoList.stream()
                .map(message -> userChatMessageConverter.toBo(
                        message,
                        senderDo.getAccount(),
                        receiverDo.getAccount()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void saveUserChatMessageToRedis(UserChatLastMessageBo bo) {
        String key = MessageConstant.CHAT_MESSAGE_KEY + bo.senderId + ":" + bo.receiverId + ":";
        redisService.setObject(key, bo, MessageConstant.CHAT_MESSAGE_EXPIRE_TIME);
    }

    @Override
    public void saveUserChatMessagesToDatabase(List<UserChatMessageDo> dos) {
        // 取消存储到mysql
//        chatMapper.batchInsert(dos);
        messageStorageService.storeMessagesToDatabase(dos);
    }

    private long getUserId(String account){
        Long userId = userService.getIdByAccount(account);
        if (userId == null){
            String errorMsg = String.format("account：%s 不存在", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        return userId;
    }
}
