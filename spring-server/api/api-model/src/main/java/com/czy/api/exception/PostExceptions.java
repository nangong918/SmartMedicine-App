package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum PostExceptions implements ExceptionEnums {

    // 帖子不存在
    POST_NOT_EXIST("P_10001", "帖子不存在"),
    // 评论不存在
    COMMENT_NOT_EXIST("P_10002", "评论不存在"),
    // 帖子内容不合规，请修改
    POST_CONTENT_ILLEGAL("P_10003", "帖子内容不合规，请修改"),
    // 修改帖子失败，你不能不修改任何数据
    UPDATE_POST_ERROR("P_10004", "修改帖子失败，你不能不修改任何数据"),
    ;

    private final String code;
    private final String message;

    PostExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static PostExceptions getByCode(String code) {
        for (PostExceptions value : PostExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
