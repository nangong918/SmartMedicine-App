package com.czy.domain.fragmentActivityAo;

import androidx.lifecycle.MutableLiveData;

import com.czy.domain.vo.entity.message.ChatContactListVo;

// MessageFragment的Vo
public class MessageVo {

    // RecyclerView的ListVo
    public ChatContactListVo chatContactListVo = new ChatContactListVo();

    // 未读消息数量
    public MutableLiveData<Integer> unreadMessageCountLd = new MutableLiveData<>(0);
    // 总消息数量
    public MutableLiveData<Integer> totalMessageCountLd = new MutableLiveData<>(0);
}
