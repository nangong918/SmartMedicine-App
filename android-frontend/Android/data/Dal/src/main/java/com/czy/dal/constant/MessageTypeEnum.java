package com.czy.dal.constant;

public enum MessageTypeEnum {
    text(0, "text"),
    image(1, "image"),
    video(2, "video"),
    audio(3, "audio"),
    file(4, "file"),
    ;

    public final int code;
    public final String name;

    MessageTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    // code -> o
    public static MessageTypeEnum getMessageTypeEnum(int code) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.code == code) {
                return messageTypeEnum;
            }
        }
        return null;
    }

    // name -> o
    public static MessageTypeEnum getMessageTypeEnum(String name) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.name.equals(name)) {
                return messageTypeEnum;
            }
        }
        return null;
    }

}
