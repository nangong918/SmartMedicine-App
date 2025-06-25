package com.czy.dal.vo.fragmentActivity.chat;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.vo.entity.message.ChatMessageItemVo;

import java.util.LinkedList;
import java.util.List;

public class ChatListVo {

    public final MutableLiveData<List<ChatMessageItemVo>> chatMessageList = new MutableLiveData<>(new LinkedList<>());

}
