package com.czy.api.constant.feature;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/13 9:41
 * 未知 0
 * 点赞 1
 * 收藏 2
 * 转发 3
 * （注意，评论涉及nlp，单独提出做情感分析，不在此枚举）
 * 取消点赞 -1
 * 取消收藏 -2
 */
@Getter
public enum PostOperateTypeEnum {

    UNKNOWN("未知", 0),

    LIKE("点赞", 1),

    COLLECT("收藏", 2),

    FORWARD("转发", 3),

    // 取消点赞 -1
    CANCEL_LIKE("取消点赞", 4),
    // 取消收藏 -2
    CANCEL_COLLECT("取消收藏", 5),
    ;

    private final String name;

    private final Integer code;

    PostOperateTypeEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    // code -> o
    public static PostOperateTypeEnum getByCode(Integer code) {
        for (PostOperateTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }


}
