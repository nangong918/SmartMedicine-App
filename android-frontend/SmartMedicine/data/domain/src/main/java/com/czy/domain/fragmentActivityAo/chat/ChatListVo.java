package com.czy.domain.fragmentActivityAo.chat;

import com.czy.domain.vo.entity.message.ChatMessageItemVo;

import java.util.LinkedList;
import java.util.List;

public class ChatListVo {
    // 消息插入多，查询少
    public List<ChatMessageItemVo> chatMessageList = new LinkedList<>();

}
