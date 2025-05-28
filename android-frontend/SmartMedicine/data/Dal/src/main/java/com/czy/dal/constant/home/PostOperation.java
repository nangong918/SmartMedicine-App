package com.czy.dal.constant.home;


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
 * 7.取消不感兴趣
 */
public enum PostOperation {
    NULL("未知", 0),
    LIKE("点赞", 1),
    CANCEL_LIKE("取消点赞", 2),
    COLLECT("收藏",3),
    CANCEL_COLLECT("取消收藏", 4),
    FORWARD("转发",5),
    NOT_INTERESTED("不喜欢推荐", 6),
    CANCEL_NOT_INTERESTED("取消不喜欢推荐", 7)
    ;

    private final String name;
    private final Integer code;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    PostOperation(String name, Integer code) {
        this.code = code;
        this.name = name;
    }

}
