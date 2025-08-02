package com.czy.appcore.service.chat;


import androidx.annotation.NonNull;

import com.czy.dal.constant.NettyConstants;

/**
 * 用于记录当前chatActivity上下文的工具
 */
public class CurrentChatMessageContext {
    public Long contactId;
    public OnChatMessageChange onChatMessageChange;

    public CurrentChatMessageContext(@NonNull Long contactId, @NonNull OnChatMessageChange onChatMessageChange) {
        this.contactId = contactId;
        this.onChatMessageChange = onChatMessageChange;
    }

    public boolean isEmpty(){
        return contactId == null || contactId.equals(NettyConstants.ERROR_ID);
    }
}
