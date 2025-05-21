package com.czy.api.constant.search.result;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/14 16:01
 * 0 未识别
 * 1 未在识别范围
 * 2 识别到意图
 * 3 数据不完善
 */
@Getter
public enum PersonalResultIntent {
    UNRECOGNIZED(0, "未识别"),
    NOT_IN_RANGE(1, "未在识别范围"),
    RECOGNIZED(2, "识别到意图"),
    DATA_INCOMPLETE(3, "数据不完善"),
    ;

    private final Integer type;
    private final String name;

    PersonalResultIntent(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static PersonalResultIntent getByType(Integer type) {
        for (PersonalResultIntent value : PersonalResultIntent.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return UNRECOGNIZED;
    }

    // code -> o
    public static PersonalResultIntent getByName(String name) {
        for (PersonalResultIntent value : PersonalResultIntent.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return UNRECOGNIZED;
    }
}
