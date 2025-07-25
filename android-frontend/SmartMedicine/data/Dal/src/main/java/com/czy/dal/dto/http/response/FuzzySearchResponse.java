package com.czy.dal.dto.http.response;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.search.FuzzySearchResponseEnum;

/**
 * @author 13225
 * @date 2025/4/30 18:29
 */
public class FuzzySearchResponse implements BaseBean {
    /**
     * 结果类型：
     * 1：0~1级 mysql like搜索结果
     * 2：2级 IK/Jieba分词 + elasticsearch搜索结果
     * 3：3级 AcTree实体命名识别 + Neo4j图临近相似实体搜索结果
     * 4：4级 user context特征向量 + Bert意图识别 + AcTree推荐搜索结果
     * 0：非自然语言；99：寒暄；100：App功能问题
     * <p>
     * 默认是：非自然语言结果
     */
    public Integer type = FuzzySearchResponseEnum.NO_RESULT.getType();
    /**
     * 数据；由于有多种数据源，所以用Object存储
     * ERROR_RESULT -> String 返回错误信息
     * NO_RESULT -> null 无结果
     * NOT_NATURAL_LANGUAGE_RESULT -> String 返回提示回复
     * SEARCH_POST_RESULT -> PostSearchResultAo 搜索结果
     * @see com.czy.dal.ao.search.PostSearchResultAo
     * TALK_RESULT -> String 寒暄
     * QUESTION_RESULT -> QuestionAo  问题回答 （postList + answer）
     * @see com.czy.dal.ao.search.QuestionAo
     * RECOMMEND_QUESTION_RESULT -> PostRecommendAo 推荐问题
     * @see com.czy.dal.ao.search.PostRecommendAo
     * APP_FUNCTION_RESULT -> AppFunctionAo  App功能问题
     * @see com.czy.dal.ao.search.AppFunctionAo
     * PERSONAL_QUESTION_RESULT -> PersonalEvaluateAo 个人问题
     * @see com.czy.dal.ao.search.PersonalEvaluateAo
     */
    public Object data;
}
