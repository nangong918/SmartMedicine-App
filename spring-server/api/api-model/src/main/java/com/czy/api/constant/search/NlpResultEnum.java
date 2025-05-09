package com.czy.api.constant.search;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/5/9 11:23
 * plan C
 * 句子 -> bert-nlj识别是否是自然语言（准确率几乎100%）
 * 句子 -> bert-nlu意图识别模型：（标题检索；询问问题；寒暄）
 * 检索分支：
 * <p>      精确
 *      0~1级：mysql的like
 *      2级：elasticsearch的tokenized
 * <p>      模糊 + user context vector
 *      3级：neo4j规则集 + es查询 + user context vector排序
 *      4级：neo4j疾病相似度查询 + user context vector排序
 *      5级：neo4j帖子相似度查询 + user context vector排序（类推荐系统）
 * <p>
 * 问答分支：
 *      疾病属性问题集合：
 *          定义
 *          病因
 *          预防
 *          临床表现(病症表现)
 *          相关病症
 *          治疗方法
 *          所属科室
 *          传染性
 *          治愈率
 *          禁忌
 *          化验/体检方案
 *          治疗时间
 *      症状问诊意图
 *          多个症状进行共同疾病搜索
 *      推荐
 *          推荐内容检索 + post评分排序 + user context vector排序
 *      个人评价
 *          收集用户数据回答（帖子特征 + 用户健康数据 + 医疗预测结果）
 *      App问题
 *          识别出是App问题进入App规则集回答，如果规则集没有数据则回答不知道
 */
@Getter
public enum NlpResultEnum {

    /**
     * -1.无结果、Error，异常等
     * 0.非自然语言
     * 1.寒暄
     * 从自然语言角度出发,问题意图和搜索意图是重叠的,所以分为以下情况:
     * 2.搜索意图（问题中识别为其他的时候）
     * 问题意图或者搜索意图（同时返回问诊和搜索）：
     *  3.推荐请求
     *  4.个人评价请求
     *  5.App问题
     *  6.多个症状进行共同疾病搜索
     *          7.定义
     *          8.病因
     *          9.预防
     *          10.临床表现(病症表现)
     *          11.相关病症
     *          12.治疗方法
     *          13.所属科室
     *          14.传染性
     *          15.治愈率
     *          16.禁忌
     *          17.化验/体检方案
     *          18.治疗时间
     */

    NONE(-1, "无结果、Error，异常等"),
    NOT_NL(0, "非自然语言"),
    GREETING(1, "寒暄"),
    SEARCH(2, "搜索意图（问题中识别为其他的时候）"),
    RECOMMEND(3, "问题意图:推荐请求"),
    PERSONAL_EVALUATION(4, "问题意图:个人评价请求"),
    APP_QUESTION(5, "问题意图:App问题"),
    SYMPTOM_SEARCH_QUESTION(6, "问题意图:症状问诊:多个症状进行共同疾病搜索"),
    DISEASE_DEFINITION(7, "问题意图:疾病问诊:定义"),
    DISEASE_CAUSE(8, "问题意图:疾病问诊:病因"),
    DISEASE_PREVENTION(9, "问题意图:疾病问诊:预防"),
    DISEASE_SYMPTOM(10, "问题意图:疾病问诊:临床表现(病症表现)"),
    DISEASE_RELATED_SYMPTOM(11, "问题意图:疾病问诊:相关病症"),
    DISEASE_TREATMENT(12, "问题意图:疾病问诊:治疗方法"),
    DISEASE_DEPARTMENT(13, "问题意图:疾病问诊:所属科室"),
    DISEASE_INFECTION(14, "问题意图:疾病问诊:传染性"),
    DISEASE_CURE_RATE(15, "问题意图:疾病问诊:治愈率"),
    DISEASE_TABOO(16, "问题意图:疾病问诊:禁忌"),
    DISEASE_LABORATORY(17, "问题意图:疾病问诊:化验/体检方案"),
    DISEASE_TREATMENT_TIME(18, "问题意图:疾病问诊:治疗时间");
    private final int code;
    private final String desc;
    NlpResultEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // code -> o
    public static NlpResultEnum getByCode(int code) {
        for (NlpResultEnum value : NlpResultEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NONE;
    }

}
