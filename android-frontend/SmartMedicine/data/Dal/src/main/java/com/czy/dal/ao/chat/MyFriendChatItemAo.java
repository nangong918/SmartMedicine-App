package com.czy.dal.ao.chat;


import com.czy.dal.entity.ChatEntity;
import com.czy.dal.entity.UserViewEntity;

public class MyFriendChatItemAo {
    // 最后一条消息
    public ChatEntity lastChatMessage;

    // 未读消息条数
    public Integer unreadCount = 0;

    // 用户View
    public UserViewEntity userViewEntity;

    // 时间戳
    public Long timestamp;
}
