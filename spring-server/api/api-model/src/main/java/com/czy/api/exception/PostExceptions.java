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
    // 评论失败
    COMMENT_ERROR("P_10005", "评论失败"),
    // 删除评论失败
    DELETE_COMMENT_ERROR("P_10006", "删除评论失败"),
    // 不能发布空评论
    EMPTY_COMMENT_ERROR("P_10007", "不能发布空评论"),
    // 回复的内容已被删除
    REPLY_COMMENT_DELETED("P_10008", "回复的内容已被删除"),
    // 没有评论的权限
    NO_COMMENT_PERMISSION("P_10009", "没有评论的权限"),
    // 对帖子操作行为异常
    OPERATION_TYPE_NOT_EXIST("P_10010", "对帖子操作行为异常"),
    // 收藏夹操作失败
    COLLECT_FOLDER_OPERATION_FAILED("P_10011", "收藏夹操作失败"),
    // 创建收藏夹失败
    CREATE_COLLECT_FOLDER_FAILED("P_10012", "创建收藏夹失败"),
    // 发布相同帖子内容被驳回
    POST_CONTENT_REJECTED("P_10013", "发布相同帖子内容被驳回"),
    // 帖子内容违规，请修改
    POST_CONTENT_VIOLATION("P_10014", "帖子内容违规，请修改"),
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
