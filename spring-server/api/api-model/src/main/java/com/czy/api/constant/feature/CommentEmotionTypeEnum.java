package com.czy.api.constant.feature;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/13 11:06
 * 积极肯定 0
 * 中立 1
 * 消极否定 2
 */


@Getter
public enum CommentEmotionTypeEnum {

    UNKNOWN("未知", -1),
    POSITIVE("积极", 0),
    NEUTRAL("中立", 1),
    NEGATIVE("消极", 2)
    ;

    private final String name;
    private final Integer code;

    CommentEmotionTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public static CommentEmotionTypeEnum getByCode(Integer code) {
        for (CommentEmotionTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }

}
