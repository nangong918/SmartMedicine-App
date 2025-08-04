package com.czy.appcore.service.chat;

import com.czy.dal.ao.chat.ChatContactItemAo;

import java.util.List;

public interface OnRecentContactMessageChange {
    void onChange(List<ChatContactItemAo> list);
}
