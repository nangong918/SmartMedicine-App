package com.czy.dal.constant.home;

public enum RecommendButtonType {
    // null
    NULL(0),
    // 点赞
    LIKE(1),

    // 收藏
    COLLECT(2),

    // 不喜欢
    DISLIKE(3);

    public final int value;

    RecommendButtonType(int value) {
        this.value = value;
    }

    // value -> o
    public static RecommendButtonType valueOf(int value) {
        for (RecommendButtonType buttonType : RecommendButtonType.values()) {
            if (buttonType.value == value) {
                return buttonType;
            }
        }
        return NULL;
    }

}
