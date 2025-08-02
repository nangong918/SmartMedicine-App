package com.czy.appcore.service.chat;

import com.czy.baseUtilsLib.algorithm.SortItem;
import com.czy.dal.bo.UserChatMessageBo;
import com.czy.dal.constant.MessageTypeEnum;
import com.czy.dal.dto.netty.forwardMessage.SendTextDataRequest;
import com.czy.dal.dto.netty.forwardMessage.UserImageResponse;
import com.czy.dal.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.dal.vo.entity.message.ChatMessageItemVo;

import java.util.Optional;

public class MessageItem extends SortItem {
    public Long senderId;
    public Long receiverId;
    public String content;
    public Integer messageType;
    public Long timestamp;

    public ChatMessageItemVo toChatMessageItemVo(Long myId){
        ChatMessageItemVo chatMessageItemVo = new ChatMessageItemVo();
        chatMessageItemVo.content = content;
        chatMessageItemVo.setTimeByStringTimeStamp(timestamp);
        chatMessageItemVo.viewType = Optional.ofNullable(myId)
                .map(id -> {
                    if (id.equals(senderId)){
                        return ChatMessageItemVo.VIEW_TYPE_SENDER;
                    }
                    else {
                        return ChatMessageItemVo.VIEW_TYPE_RECEIVER;
                    }
                }).orElse(ChatMessageItemVo.VIEW_TYPE_RECEIVER);
        chatMessageItemVo.isRead = false;
        return chatMessageItemVo;
    }

    public static MessageItem getByChatMessageItemVo(UserChatMessageBo bo){
        MessageItem messageItem = new MessageItem();
        messageItem.content = bo.msgContent;
        messageItem.messageType = bo.msgType;
        messageItem.receiverId = bo.receiverId;
        messageItem.senderId = bo.senderId;
        messageItem.timestamp = bo.timestamp;
        messageItem.index = bo.timestamp;
        return messageItem;
    }

    public static MessageItem getBySendTextDataRequest(SendTextDataRequest request){
        MessageItem messageItem = new MessageItem();
        messageItem.content = request.getContent();
        messageItem.messageType = MessageTypeEnum.text.code;
        messageItem.receiverId = request.getReceiverId();
        messageItem.senderId = request.getSenderId();
        messageItem.timestamp = Optional.ofNullable(request.getTimestamp())
                .map(t -> {
                    try {
                        return Long.valueOf(t);
                    } catch (Exception e) {
                        return System.currentTimeMillis();
                    }
                })
                .orElse(System.currentTimeMillis());
        messageItem.index = messageItem.timestamp;
        return messageItem;
    }

    public static MessageItem getByUserTextDataResponse(UserTextDataResponse response){
        MessageItem messageItem = new MessageItem();
        messageItem.content = response.getContent();
        messageItem.messageType = MessageTypeEnum.text.code;
        messageItem.receiverId = response.getReceiverId();
        messageItem.senderId = response.getSenderId();
        messageItem.timestamp = Optional.ofNullable(response.getTimestamp())
                .map(t -> {
                    try {
                        return Long.valueOf(t);
                    } catch (Exception e) {
                        return System.currentTimeMillis();
                    }
                })
                .orElse(System.currentTimeMillis());
        messageItem.index = messageItem.timestamp;
        return messageItem;
    }

    public static MessageItem getByUserImageResponse(UserImageResponse response){
        MessageItem messageItem = new MessageItem();
        messageItem.content = response.imageUrl;
        messageItem.messageType = MessageTypeEnum.image.code;
        messageItem.receiverId = response.getReceiverId();
        messageItem.senderId = response.getSenderId();
        messageItem.timestamp = Optional.ofNullable(response.getTimestamp())
                .map(t -> {
                    try {
                        return Long.valueOf(t);
                    } catch (Exception e) {
                        return System.currentTimeMillis();
                    }
                })
                .orElse(System.currentTimeMillis());
        messageItem.index = messageItem.timestamp;
        return messageItem;
    }
}