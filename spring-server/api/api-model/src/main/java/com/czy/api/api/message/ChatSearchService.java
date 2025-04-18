package com.czy.api.api.message;

import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.Do.message.UserChatMessageEsDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 18:11
 */
public interface ChatSearchService {

    // 查询senderId和关键词keyword的聊天记录
    List<UserChatMessageEsDo> searchUserChatMessage(Long senderId, String keyword);

    // 查询senderId和receiverId和关键词keyword的聊天记录
    List<UserChatMessageEsDo> searchUserChatMessage(Long senderId, Long receiverId, String keyword);

    // 查询senderId和关键词keyword的聊天记录 + Page分页限制
    List<UserChatMessageEsDo> searchUserChatMessageLimit(Long senderId, String keyword, Integer page);

    // 查询senderId和receiverId和关键词keyword的聊天记录 + Page分页限制
    List<UserChatMessageEsDo> searchUserChatMessageLimit(Long senderId, Long receiverId, String keyword, Integer page);

}
