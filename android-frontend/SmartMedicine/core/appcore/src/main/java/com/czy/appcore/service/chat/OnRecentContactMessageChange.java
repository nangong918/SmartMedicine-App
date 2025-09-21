package com.czy.appcore.service.chat;

import com.czy.domain.ao.message.ChatContactItemAo;

import java.util.List;

public interface OnRecentContactMessageChange {
    void onChange(List<ChatContactItemAo> list);
}
