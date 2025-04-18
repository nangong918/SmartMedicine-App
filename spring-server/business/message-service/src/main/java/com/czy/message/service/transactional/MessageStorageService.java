package com.czy.message.service.transactional;

import com.czy.api.domain.Do.message.UserChatMessageDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 15:21
 */
public interface MessageStorageService {

    // 存储消息到数据库
    void storeMessagesToDatabase(List<UserChatMessageDo> messages);

}
