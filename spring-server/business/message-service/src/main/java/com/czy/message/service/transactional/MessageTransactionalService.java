package com.czy.message.service.transactional;

import com.czy.api.domain.Do.message.UserChatMessageDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 15:50
 */
public interface MessageTransactionalService {

    // save text
    void saveTextMessage(List<UserChatMessageDo> messages);

    // save file
    void saveFileMessage(List<UserChatMessageDo> messages);

}
