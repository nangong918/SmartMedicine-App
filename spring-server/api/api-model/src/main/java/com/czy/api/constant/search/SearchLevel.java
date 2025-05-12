package com.czy.api.constant.search;


import lombok.Getter;

/**
 * @author chenzy
 * @data 2025/5/12
 * -1级：不匹配
 * 0级：完全匹配
 * 1级：like模糊匹配
 * 2级：es包含匹配
 * 3级：neo4j实体规则集匹配
 * 4级：neo4j实体相似匹配
 * 5级：nlp相似匹配
 * code + name
 */
@Getter
public enum SearchLevel {

    MINUS_ONE(-1, "不匹配"),
    ZERO(0, "完全匹配"),
    ONE(1, "like模糊匹配"),
    TWO(2, "es包含匹配"),
    THREE(3, "neo4j实体规则集匹配"),
    FOUR(4, "neo4j实体相似匹配"),
    FIVE(5, "nlp相似匹配");

    private final Integer code;
    private final String name;

    SearchLevel(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SearchLevel getByCode(Integer code) {
        for (SearchLevel value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    // code -> o
    public static SearchLevel getByCode(int code) {
        for (SearchLevel value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return MINUS_ONE;
    }

}
