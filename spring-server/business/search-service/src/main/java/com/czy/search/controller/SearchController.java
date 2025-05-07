package com.czy.search.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.post.PostNerService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.es.FieldAnalyzer;
import com.czy.api.constant.search.FuzzySearchResponseEnum;
import com.czy.api.constant.search.SearchConstant;
import com.czy.api.domain.Do.test.TestSearchEsDo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.ao.search.PostSearchResultAo;
import com.czy.api.domain.dto.http.request.FuzzySearchRequest;
import com.czy.api.domain.dto.http.response.FuzzySearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author 13225
 * @date 2025/4/30 18:13
 * 暂时使用Http直接网络请求python
 * TODO 后续改为RPC
 * 职责分析：
 * Java有post，user环境
 * 最后意图要落实在具体的实现上，
 * Java负责开始提供输入，user向量
 * python负责数据处理，返回Java决策
 * Java负责最后将决策转为实体，返回前端。
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RequiredArgsConstructor // 自动注入@Autowired
@RestController
@RequestMapping(SearchConstant.MainSearch_CONTROLLER)
public class SearchController {

    // mainSearch，包括全部的模糊搜索。依赖全部business模块
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostNerService postNerService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;

    /**
     * 模糊搜索
     * @param request  模糊搜索的句子 + userId（用于userContext特征上下文）
     * @return  搜索结果
     */
    @PostMapping("/fuzzy")
    public FuzzySearchResponse fuzzySearch(@Valid FuzzySearchRequest request) {
        // 搜索这种消耗资源的操作需要给用户上限流和分布式锁
        FuzzySearchResponse response = new FuzzySearchResponse();
        String sentence = request.getSentence();
        // 略过python搜索响应
        // 到此处了说明一定是post
        response.setType(FuzzySearchResponseEnum.SEARCH_POST_RESULT.getType());
        PostSearchResultAo postSearchResultAo = new PostSearchResultAo();

        // 0~1级搜索 到此处说明sentence本身就是title，所以likeTitle传递sentence
        List<Long> likePostIdList = likeSearch(sentence);
        // 2级搜索 搜索：AcTree匹配实体 + ElasticSearch搜索
        // 缓存结结果，避免后续搜索调用两次
        List<PostNerResult> nerResults = new ArrayList<>();
        List<Long> tokenizedPostIdList = tokenizedSearch(sentence, nerResults);
        // TODO 3级别：Neo4j查询实体相似度，要求此实体必须存在实体，列出此实体的top-k相似实体，然后找到post
        // TODO 4级别：Bert意图识别；帖子还能如何分类？首先先将帖子分类；存入帖子的时候调用bert模型将post标签分类
        //  用户查询帖子的是时候，也对句子按照post进行分类，得到系列接股票；用user context对结果进行按照用户感兴趣顺序排序
        // TODO 5级别：问题回复
        // 基于内容的问题，存在特征不清晰的情况，就比方说，如果帖子基本全都是非专业属于的分享帖子，标签大量标签相同，属于是特征工程做的不到位。系统中不应该存在大量特征相同的。
        // neo4j相似度搜索
        return null;
    }

    /**
     * 0~1级 的like搜索
     * @param title 搜索的标题
     * @return      搜索结果
     */
    private List<Long> likeSearch(String title){
        return postSearchService.searchPostIdsByLikeTitle(title);
    }


    // TODO 用python给IK导出一份词典！！！！！！！！！！！！！！！！！！！！！！！！
    private List<Long> tokenizedSearch(String title, List<PostNerResult> results){
        List<PostNerResult> nerResults = postNerService.getPostNerResults(title);
        results.clear();
        // 缓存结结果，避免后续搜索调用两次
        results.addAll(nerResults);
        if (nerResults.isEmpty()){
            // 如果没有词典中的词，则返回空
            return new ArrayList<>();
        }
        // 有词典关键词的情况
        return postSearchService.searchPostIdsByTokenizedTitle(title);
    }

    // 制定规则集
    /**
     * 检查句子中是否存在（疾病/症状实体）
     * 1.疾病实体：
     *      1. 如果某种疾病存在伴随疾病，则搜索（疾病 + 伴随疾病）
     *      2. 疾病如果伴随某些症状，则搜索（疾病 + 症状）
     *      3. 如果继斌存在解决方案：药品，食物，菜谱，则直接搜索这些
     *      4. 疾病跟某科室相关，查询（科室 + 疾病）
     * 2. 症状
     *      1. 如果包含多个症状，则症状的集合匹配是否存在疾病。
     */

    /**
     * 相似度查询
     * 疾病的Jacard相似度
     * 疾病之间的共同邻居相似度
     * 疾病之间的距离相似度
     */

    /**
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
}
