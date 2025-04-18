package com.czy.message.service;




import com.czy.api.api.user.UserService;
import com.czy.api.constant.message.ChatConstant;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.ao.message.FetchUserMessageAo;
import com.czy.api.domain.bo.message.UserChatLastMessageBo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import com.czy.api.api.message.ChatService;
import com.czy.message.mapper.mysql.UserChatMessageMapper;
import com.czy.message.service.transactional.MessageStorageService;
import com.czy.springUtils.service.RedisService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;

    public static final Long CHAT_MESSAGE_EXPIRE_TIME = 60 * 60 * 24 * 7L;

    public static final int MAX_RECENT_MESSAGE_COUNT = 200;
    public static final int MAX_SEARCH_MESSAGE_LIMIT = 50;

    @Override
    public List<UserChatLastMessageBo> getUserAllChatMessage(String senderAccount) {
        List<UserChatLastMessageBo> messages = new LinkedList<>();
        // 获取所有相关的键 考虑到senderId可能是receiverId
//        Set<String> keys = redisService.getKeys(ChatConstant.CHAT_MESSAGE_KEY + senderId + ":");
        // 此时的sender是想要查询sender收到的消息；所以sender要作为receiver；所以
        String checkKey = ChatConstant.CHAT_MESSAGE_KEY + "*:" + senderAccount;
        Set<String> keysReceiver = redisService.getKeys(checkKey);

        for (String key : keysReceiver) {
            UserChatLastMessageBo message = redisService.getObject(key, UserChatLastMessageBo.class);
            if (message != null) {
                messages.add(message);
            }
        }

        // 限制返回的消息数量 (交给前端去根据时间顺序排序，节省后端算力和时间)
        return messages.size() > MAX_RECENT_MESSAGE_COUNT ? messages.subList(0, MAX_RECENT_MESSAGE_COUNT) : messages;
    }

    @Override
    public UserChatLastMessageBo getUserChatMessage(String senderAccount, String receiverAccount) {
        String key = ChatConstant.CHAT_MESSAGE_KEY + senderAccount + ":" + receiverAccount + ":";
        return redisService.getObject(key, UserChatLastMessageBo.class);
    }

    @Override
    public void clearUserChatMessageUnreadCount(String senderAccount, String receiverAccount) {
        String key = ChatConstant.CHAT_MESSAGE_KEY + senderAccount + ":" + receiverAccount + ":";
        UserChatLastMessageBo bo = redisService.getObject(key, UserChatLastMessageBo.class);
        if (bo != null){
            bo.setUnreadCount(0);
            redisService.setObject(key, bo, CHAT_MESSAGE_EXPIRE_TIME);
        }
        else {
            log.warn("bo == null");
        }
    }

    @Override
    public List<UserChatMessageBo> getUserChatMessage(FetchUserMessageAo fetchUserMessageAo) {
        // 限制 messageCount 最大值为 200
        int messageCount = Math.min(fetchUserMessageAo.getMessageCount(), MAX_SEARCH_MESSAGE_LIMIT);

        long senderId = getUserId(fetchUserMessageAo.getSenderAccount());
        long receiverId = getUserId(fetchUserMessageAo.getReceiverAccount());

        // 根据 timestampIndex 和 messageCount 查询用户聊天记录
        return chatMapper.selectMessagesBefore(
                senderId,
                receiverId,
                fetchUserMessageAo.getTimestampIndex(),
                messageCount
        );
    }

    @Override
    public void saveUserChatMessageToRedis(UserChatLastMessageBo userChatLastMessageBo) {
        String key = ChatConstant.CHAT_MESSAGE_KEY + userChatLastMessageBo.senderAccount + ":" + userChatLastMessageBo.receiverAccount + ":";
        redisService.setObject(key, userChatLastMessageBo, CHAT_MESSAGE_EXPIRE_TIME);
    }

    @Override
    public void saveUserChatMessagesToDatabase(List<UserChatMessageDo> userChatMessageDoList) {
        // 取消存储到mysql
//        chatMapper.batchInsert(userChatMessageDoList);
        messageStorageService.storeMessagesToDatabase(userChatMessageDoList);
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
