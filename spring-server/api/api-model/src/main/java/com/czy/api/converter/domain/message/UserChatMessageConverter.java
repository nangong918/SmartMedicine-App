package com.czy.api.converter.domain.message;

import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.api.domain.ao.message.FetchUserMessageAo;
import com.czy.api.domain.bo.message.UserChatMessageBo;
import com.czy.api.domain.dto.http.request.FetchUserMessageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
        UserChatMessageBo bo = new UserChatMessageBo();
        bo.setId(userChatMessageDo.getId());
        bo.setMsgContent(userChatMessageDo.getMsgContent());
        bo.setMsgType(userChatMessageDo.getMsgType());
        bo.setSenderAccount(senderAccount);
        bo.setReceiverAccount(receiverAccount);
        bo.setSenderId(userChatMessageDo.getSenderId());
        bo.setReceiverId(userChatMessageDo.getReceiverId());
        bo.setTimestamp(userChatMessageDo.getTimestamp());
        bo.setMsgFileId(userChatMessageDo.getMsgFileId());
        bo.setMsgFileUrl(userChatMessageDo.getMsgFileUrl());
        return bo;
    }

    // bo -> do
    default UserChatMessageDo toDo(UserChatMessageBo bo, Long senderId, Long receiverId){
        UserChatMessageDo userChatMessageDo = new UserChatMessageDo();
        userChatMessageDo.setId(bo.getId());
        userChatMessageDo.setMsgContent(bo.getMsgContent());
        userChatMessageDo.setMsgType(bo.getMsgType());
        userChatMessageDo.setSenderId(senderId);
        userChatMessageDo.setReceiverId(receiverId);
        userChatMessageDo.setTimestamp(bo.getTimestamp());
        userChatMessageDo.setMsgFileId(bo.getMsgFileId());
        userChatMessageDo.setMsgFileUrl(bo.getMsgFileUrl());
        return userChatMessageDo;
    }

    // FetchUserMessageRequest -> FetchUserMessageAo
    @Mapping(source = "senderId", target = "senderId")
    @Mapping(source = "receiverId", target = "receiverId")
    @Mapping(source = "timestampIndex", target = "timestampIndex")
    @Mapping(source = "messageCount", target = "messageCount")
    FetchUserMessageAo fetchUserMessageRequestToAo(FetchUserMessageRequest request);
}
