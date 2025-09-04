package com.czy.appview.view.home;

public enum HomeViewPagerEnum {

    // 推荐
    RECOMMEND(0),
    // 热门
    POPULAR(1),
    // 关注
    FOLLOW(2),
    // 朋友圈
    FRIEND_CIRCLE(3),

    ;
    private final int index;
    HomeViewPagerEnum(int index) {
        this.index = index;
    }

    // index
    public int getIndex() {
        return index;
    }

    // index -> o
    public static HomeViewPagerEnum getEnumByIndex(int index) {
        for (HomeViewPagerEnum value : HomeViewPagerEnum.values()) {
            if (value.index == index) {
                return value;
            }
        }
        return RECOMMEND;
    }

    public static int getCount() {
        return HomeViewPagerEnum.values().length;
    }

}
