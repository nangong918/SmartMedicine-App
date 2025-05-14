package com.czy.api.constant.search.result;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/14 15:38
 * 0 未识别
 * 1 无数据
 * 2 存在数据
 */
@Getter
public enum PostRecommendResult {
    NO_RECOMMEND(0, "未识别"),
    NO_DATA(1, "无数据"),
    HAS_DATA(2, "存在数据");

    private final Integer code;
    private final String message;

    PostRecommendResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static PostRecommendResult getByCode(Integer code) {
        for (PostRecommendResult result : values()) {
            if (result.code.equals(code)) {
                return result;
            }
        }
        return NO_RECOMMEND;
    }
}
