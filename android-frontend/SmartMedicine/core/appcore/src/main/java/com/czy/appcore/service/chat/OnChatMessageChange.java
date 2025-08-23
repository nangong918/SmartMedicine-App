package com.czy.appcore.service.chat;

import java.util.List;

public interface OnChatMessageChange {
    void onChange(List<MessageItem> list);
}
