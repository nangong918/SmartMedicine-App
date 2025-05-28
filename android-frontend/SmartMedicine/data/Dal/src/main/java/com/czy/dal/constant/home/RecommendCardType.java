package com.czy.dal.constant.home;

public enum RecommendCardType {
    // 单个大卡片
    SINGLE_BIG_CARD(1),
    // 两个小卡片
    TWO_SMALL_CARD(2);

    public final int value;
    RecommendCardType(int value) {
        this.value = value;
    }

    // code -> o
    public static RecommendCardType valueOf(int value) {
        for (RecommendCardType type : RecommendCardType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return SINGLE_BIG_CARD;
    }

}
