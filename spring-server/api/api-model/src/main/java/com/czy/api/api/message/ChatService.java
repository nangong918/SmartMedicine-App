package com.czy.api.api.message;



import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.ao.message.FetchUserMessageAo;
import com.czy.api.domain.bo.message.UserChatLastMessageBo;
import com.czy.api.domain.bo.message.UserChatMessageBo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/2/26 17:40
 */
public interface ChatService {

    // 拉取用户的全部聊天消息(限制1000条，超过就流式传输)：某个用户跟所有用户的1条最新消息List
    List<UserChatLastMessageBo> getUserAllChatMessage(Long senderId);
    // 拉取和某个用户的消息
    UserChatLastMessageBo getUserChatMessage(Long senderId, Long receiverId);
    // 清空某条未读消息的数量
    void clearUserChatMessageUnreadCount(Long senderId, Long receiverId);
    // 拉取用户和某个用户全部聊天消息(分页：一次拉取50条最新聊天消息)
    List<UserChatMessageBo> getUserChatMessage(FetchUserMessageAo fetchUserMessageAo);
    // 单条消息存入Redis
    void saveUserChatMessageToRedis(UserChatLastMessageBo userChatLastMessageBo);
    // 消息批量存储入MySQL + Mongo + ElasticSearch
    void saveUserChatMessagesToDatabase(List<UserChatMessageDo> userChatMessageDoList);
}
