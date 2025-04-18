package com.czy.api.converter.domain.message;

import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/4/17 10:03
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserChatMessageConverter {

    UserChatMessageConverter INSTANCE = Mappers.getMapper(UserChatMessageConverter.class);

    // do -> bo
    default UserChatMessageBo toBo(UserChatMessageDo userChatMessageDo, String senderAccount, String receiverAccount){
        UserChatMessageBo userChatMessageBo = new UserChatMessageBo();
        userChatMessageBo.setId(userChatMessageDo.id);
        userChatMessageBo.setMsgContent(userChatMessageDo.msgContent);
        userChatMessageBo.setMsgType(userChatMessageDo.msgType);
        userChatMessageBo.setSenderAccount(senderAccount);
        userChatMessageBo.setReceiverAccount(receiverAccount);
        userChatMessageBo.setTimestamp(userChatMessageDo.timestamp);
        return userChatMessageBo;
    }

    // bo -> do
    default UserChatMessageDo toDo(UserChatMessageBo userChatMessageBo, Long senderId, Long receiverId){
        UserChatMessageDo userChatMessageDo = new UserChatMessageDo();
        userChatMessageDo.id = userChatMessageBo.getId();
        userChatMessageDo.msgContent = userChatMessageBo.getMsgContent();
        userChatMessageDo.msgType = userChatMessageBo.getMsgType();
        userChatMessageDo.senderId = senderId;
        userChatMessageDo.receiverId = receiverId;
        userChatMessageDo.timestamp = userChatMessageBo.getTimestamp();
        return userChatMessageDo;
    }
}
