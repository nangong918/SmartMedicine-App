package com.czy.netty.constant;

import io.netty.util.AttributeKey;

public interface ChannelAttr {
    AttributeKey<Long> UID = AttributeKey.valueOf("uid");
    // 用于区分用户多端登录。后面再做
    AttributeKey<String> DEVICE_ID = AttributeKey.valueOf("device_id");
    AttributeKey<String> LANGUAGE = AttributeKey.valueOf("language");
}
