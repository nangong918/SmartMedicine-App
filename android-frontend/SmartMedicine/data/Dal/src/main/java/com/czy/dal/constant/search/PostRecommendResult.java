package com.czy.dal.constant.search;


/**
 * @author 13225
 * @date 2025/5/14 15:38
 * 0 未识别
 * 1 无数据
 * 2 存在数据
 */
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

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
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
