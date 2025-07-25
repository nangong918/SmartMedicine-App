package com.czy.dal.constant.search;


/**
 * @author 13225
 * @date 2025/5/6 13:34
 */

public enum FuzzySearchResponseEnum {
    /**
     * 结果类型：
     * 1：0~1级 mysql like搜索结果
     * 2：2级 IK/Jieba分词 + elasticsearch搜索结果
     * 3：3级 AcTree实体命名识别 + Neo4j图临近相似实体搜索结果
     * 4：4级 user context特征向量 + Bert意图识别 + AcTree推荐搜索结果
     * 0：非自然语言；99：寒暄；100：App功能问题
     * 个人问题：6
     */
    ERROR_RESULT(-2, "搜索结果异常"),
    NO_RESULT(-1, "搜索结果为空"),
    NOT_NATURAL_LANGUAGE_RESULT(0, "非自然语言：string返回，提示"),
    SEARCH_POST_RESULT(1, "各级搜索结果；PostSearchResultAo回复，结果"),
    TALK_RESULT(2, "寒暄：string返回。寒暄"),
    QUESTION_RESULT(3, "问题问题"),
    RECOMMEND_QUESTION_RESULT(4, "推荐问题：RecommendAo返回。推荐结果"),
    APP_FUNCTION_RESULT(5, "App功能问题：AppFunctionAo返回。App功能问题"),
    PERSONAL_QUESTION_RESULT(6, "个人问题：string返回。个人问题"),
    ;

    private final Integer type;
    private final String desc;

    FuzzySearchResponseEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
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
