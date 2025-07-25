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
    public Integer type = FuzzySearchResponseEnum.NOT_NATURAL_LANGUAGE_RESULT.getType();
    // 数据；由于有多种数据源，所以用Object存储
    public Object data;
}
