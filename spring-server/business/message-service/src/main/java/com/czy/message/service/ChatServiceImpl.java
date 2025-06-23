package com.czy.message.service;




import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.MessageTypeEnum;
import com.czy.api.constant.message.ChatConstant;
import com.czy.api.constant.message.MessageConstant;
import com.czy.api.converter.domain.message.UserChatMessageConverter;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.ao.message.FetchUserMessageAo;
import com.czy.api.domain.bo.message.UserChatLastMessageBo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import com.czy.api.api.message.ChatService;
import com.czy.message.mapper.mongo.UserChatMessageMongoMapper;
import com.czy.message.mapper.mysql.UserChatMessageMapper;
import com.czy.message.service.transactional.MessageStorageService;
import com.czy.springUtils.service.RedisService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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

    private final UserChatMessageMapper chatMapper;
    private final RedisService redisService;
    private final MessageStorageService messageStorageService;
    private final UserChatMessageMongoMapper userChatMessageMongoMapper;
    private final UserChatMessageConverter userChatMessageConverter;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;


    @Override
    public List<UserChatLastMessageBo> getUserAllChatMessage(Long senderId) {
        List<UserChatLastMessageBo> messages = new LinkedList<>();
        // 获取所有相关的键 考虑到senderId可能是receiverId
//        Set<String> keys = redisService.getKeys(MessageConstant.CHAT_MESSAGE_KEY + senderId + ":");
        // 此时的sender是想要查询sender收到的消息；所以sender要作为receiver；所以
        String checkKey = MessageConstant.CHAT_MESSAGE_KEY + "*:" + senderId;
        Set<String> keysReceiver = redisService.getKeys(checkKey);

        for (String key : keysReceiver) {
            UserChatLastMessageBo message = redisService.getObject(key, UserChatLastMessageBo.class);
            if (message != null) {
                messages.add(message);
            }
        }

        // 限制返回的消息数量 (交给前端去根据时间顺序排序，节省后端算力和时间)
        return messages.size() > ChatConstant.MAX_RECENT_MESSAGE_COUNT ? messages.subList(0, ChatConstant.MAX_RECENT_MESSAGE_COUNT) : messages;
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
            redisService.setObject(key, bo, ChatConstant.CHAT_MESSAGE_EXPIRE_TIME);
        }
        else {
            log.warn("bo == null");
        }
    }

    @Override
    public List<UserChatMessageBo> getUserChatMessage(FetchUserMessageAo fetchUserMessageAo) {
        // 限制 messageCount 最大值为 200
        int messageCount = Math.min(fetchUserMessageAo.getMessageCount(), ChatConstant.MAX_SEARCH_MESSAGE_LIMIT);

        long senderId = getUserId(fetchUserMessageAo.getSenderAccount());
        long receiverId = getUserId(fetchUserMessageAo.getReceiverAccount());

        // 此处不应该是mysql查询，而应该是mongodb查询
//        return chatMapper.selectMessagesAfter(
//                senderId,
//                receiverId,
//                fetchUserMessageAo.getTimestampIndex(),
//                messageCount
//        );

        // 根据 timestampIndex 和 messageCount 查询用户聊天记录
        // 资源文件存储的是 fileIdStr
        List<UserChatMessageDo> messageDoList = userChatMessageMongoMapper.findMessagesAfterTimestamp(senderId, receiverId, fetchUserMessageAo.getTimestampIndex(), messageCount);
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
                        .map(UserChatMessageDo::getMsgContent)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (Exception e){
                log.error("fileIdList 解析失败", e);
                throw new AppException("fileIdList 解析失败");
            }
            List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
            for (int i = 0; i < fileMessageDoList.size(); i++){
                UserChatMessageDo message = fileMessageDoList.get(i);
                message.setMsgContent(fileUrls.get(i));
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
                        fetchUserMessageAo.getSenderAccount(),
                        fetchUserMessageAo.getReceiverAccount()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void saveUserChatMessageToRedis(UserChatLastMessageBo userChatLastMessageBo) {
        String key = MessageConstant.CHAT_MESSAGE_KEY + userChatLastMessageBo.senderAccount + ":" + userChatLastMessageBo.receiverAccount + ":";
        redisService.setObject(key, userChatLastMessageBo, ChatConstant.CHAT_MESSAGE_EXPIRE_TIME);
    }

    @Override
    public void saveUserChatMessagesToDatabase(List<UserChatMessageDo> userChatMessageDoList) {
        // 取消存储到mysql
//        chatMapper.batchInsert(userChatMessageDoList);
        messageStorageService.storeMessagesToDatabase(userChatMessageDoList);
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
