package com.czy.api.constant.search;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/6 13:34
 */

@Getter
public enum FuzzySearchResponseEnum {
    /**
     * 结果类型：
     * 1：0~1级 mysql like搜索结果
     * 2：2级 IK/Jieba分词 + elasticsearch搜索结果
     * 3：3级 AcTree实体命名识别 + Neo4j图临近相似实体搜索结果
     * 4：4级 user context特征向量 + Bert意图识别 + AcTree推荐搜索结果
     * 0：非自然语言；99：寒暄；100：App功能问题
     */
    NOT_NATURAL_LANGUAGE_RESULT(0, "非自然语言"),
    SEARCH_POST_RESULT(1, "各级搜索结果"),
    TALK_RESULT(2, "寒暄"),
    APP_QUESTION_RESULT(3, "App功能问题");

    private final Integer type;
    private final String desc;

    FuzzySearchResponseEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    // type -> o
    public static FuzzySearchResponseEnum getByType(Integer type) {
        for (FuzzySearchResponseEnum value : FuzzySearchResponseEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
