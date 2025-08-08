package com.czy.dal.constant.home;

public enum RecommendButtonType {
    // null
    NULL(0),
    // 点赞
    LIKE(1),

    // 收藏
    COLLECT(2),

    // 不喜欢 (注意不喜欢不是点赞再点一下的取消点赞，而是用户选择少推荐，点击了灰色的心碎)
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
