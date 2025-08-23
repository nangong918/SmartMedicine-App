package com.czy.domain.constant.search;

public enum PostSearchResultListEnum {
    // like匹配结果
    LIKE_MATCH_RESULT(0, "like"),
    // tokenized匹配结果
    TOKENIZED_MATCH_RESULT(1, "tokenized"),
    // similar匹配结果
    SIMILAR_MATCH_RESULT(2, "similar"),
    // recommend匹配结果
    RECOMMEND_MATCH_RESULT(3, "recommend");
    private final int value;
    private final String name;
    PostSearchResultListEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }
    public int getValue() {
        return value;
    }
    public String getName() {
        return name;
    }
    public static PostSearchResultListEnum getEnum(int value) {
        for (PostSearchResultListEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return LIKE_MATCH_RESULT;
    }
}
