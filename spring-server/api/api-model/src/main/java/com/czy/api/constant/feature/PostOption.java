package com.czy.api.constant.feature;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/21 17:04
 * 0.null
 * 1.点赞
 * 2.取消点赞
 * 3.收藏
 * 4.取消收藏
 * 5.转发
 * 6.不感兴趣
 */
@Getter
public enum PostOption {
    NULL(0),
    LIKE(1),
    CANCEL_LIKE(2),
    COLLECT(3),
    CANCEL_COLLECT(4),
    FORWARD(5),
    NOT_INTERESTED(6);

    private final Integer code;

    PostOption(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
