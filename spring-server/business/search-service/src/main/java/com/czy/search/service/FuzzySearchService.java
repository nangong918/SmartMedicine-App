package com.czy.search.service;

import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.ao.post.PostNerResult;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/8 16:20
 */
public interface FuzzySearchService {

    /**
     * 0~1级 的like搜索
     * @param title 搜索的标题
     * @return      搜索结果
     */
    List<Long> likeSearch(String title);

    /**
     * 2级搜索 elasticsearch分词搜索
     * @param title 搜索的标题
     * @return      搜索结果
     */
    List<Long> tokenizedSearch(String title);

    /**
     * 3级搜索 neo4j规则搜索
     * 检查句子中是否存在（疾病/症状实体）
     * 1.疾病实体：
     *      1. 如果某种疾病存在伴随疾病，则搜索（疾病 + 伴随疾病）
     *      2. 疾病如果伴随某些症状，则搜索（疾病 + 症状）
     *      3. 如果疾病存在解决方案：药品，食物，菜谱
     * 2. 症状
     *      4. 如果包含多个症状，则症状的集合匹配是否存在疾病。
     * @param nerResults    ner结果
     * @return               搜索结果
     */
    List<Long> neo4jRuleSearch(List<PostNerResult> nerResults);

    /**
     * 4级搜索 相似度搜索
     * 相似度查询
     * 疾病的Jacard相似度
     * 疾病之间的共同邻居相似度
     * 疾病之间的距离相似度
     * @param nerResults    ner结果
     * @return              搜索结果
     */
    List<Long> similaritySearch(List<PostNerResult> nerResults);

    /**
     * 获取帖子信息   返回包含下载链接的ao
     * @param postIds    帖子id
     * @return            帖子信息
     */
    List<PostInfoUrlAo> getPostInfoUrlAos(List<Long> postIds);
}
