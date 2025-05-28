package com.czy.appcore.service.chat;

import java.util.List;

public interface OnMessageListChange {
    void onMessageListChange(List<MessageItem> list);
}
