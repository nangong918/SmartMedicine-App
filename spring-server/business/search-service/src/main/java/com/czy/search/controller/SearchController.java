package com.czy.search.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.post.PostNerService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.es.FieldAnalyzer;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.constant.search.FuzzySearchResponseEnum;
import com.czy.api.constant.search.SearchConstant;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.test.TestSearchEsDo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.ao.search.PostSearchResultAo;
import com.czy.api.domain.dto.http.request.FuzzySearchRequest;
import com.czy.api.domain.dto.http.response.FuzzySearchResponse;
import com.czy.search.rule.Rule1AccompanyingDiseases;
import com.czy.search.rule.Rule2AccompanyingSymptoms;
import com.czy.search.rule.Rule3DiseasesHasSuggestions;
import com.czy.search.rule.Rule4SymptomsFindDiseases;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
    private final PostConverter postConverter;

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

        // 0~1级搜索 到此处说明sentence本身就是title，所以likeTitle传递sentence;
        List<Long> likePostIdList = likeSearch(sentence);
        // 2级搜索 搜索：AcTree匹配实体 + ElasticSearch搜索;
        // 缓存结结果，避免后续搜索调用两次
        List<PostNerResult> nerResults = new ArrayList<>();
        List<Long> tokenizedPostIdList = tokenizedSearch(sentence, nerResults);


        // 3级搜索：neo4j规则集 + es查询 + user context vector排序;
        List<Long> neo4jRulePostIdList = neo4jRuleSearch(nerResults);
        // 4级搜索：neo4j疾病相似度查询 + user context vector排序;
        List<Long> similarList = similaritySearch(nerResults);

        // 转换
        postSearchResultAo.setLikePostList(getPostInfoUrlAos(likePostIdList));
        postSearchResultAo.setTokenizedPostList(getPostInfoUrlAos(tokenizedPostIdList));
        postSearchResultAo.setSimilarPostList(getPostInfoUrlAos(similarList));
        postSearchResultAo.setRecommendPostList(getPostInfoUrlAos(neo4jRulePostIdList));

        response.setType(FuzzySearchResponseEnum.SEARCH_POST_RESULT.getType());
        response.setData(postSearchResultAo);
        return response;
    }

    /**
     * user feature context 设计：
     * 1. 发布的时候用户手动打分区标签 + Bert模型对文章进行分类：#日常分享 #专业医疗知识 #养生技巧 #医疗新闻 #其他
     * 2. 根据用户行为：
     *      发布行为；
     *      显性帖子行为：
     *          点赞，
     *          评论（BERT情感分类NLE：肯定态度，否定态度，中立态度），
     *          收藏；
     *      隐性行为：
     *          点击率，
     *          浏览时长（1.根据文章长度估算大概要读取的时间 - 用户已读取的时间 2.固定判断时长：超过30秒一定增加权重）
     *          搜搜行为
     * 3. 热衰减：
     *       定时任务：每3天用户的全部权重*0.8；30天全部消失（存储在Redis自发消失）
     * 4.特征设计：
     *       1.分类（u-l）特征：
     *          用户对不同的标签的权重
     *       2.实体（u-a）特征：
     *          用户对不同的实体的特征权重
     *       3.物品（u-i）特征：
     *          用户对不同帖子的权重
     */

    /**
     * 0~1级 的like搜索
     * @param title 搜索的标题
     * @return      搜索结果
     */
    private List<Long> likeSearch(String title){
        List<Long> postIds = postSearchService.searchPostIdsByLikeTitle(title);
        if (postIds.isEmpty()){
            return new ArrayList<>();
        }
        // 去重
        return new ArrayList<>(new LinkedHashSet<>(postIds));
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
        List<Long> postIds = postSearchService.searchPostIdsByTokenizedTitle(title);
        if (postIds.isEmpty()){
            return new ArrayList<>();
        }
        // 去重
        return new ArrayList<>(new LinkedHashSet<>(postIds));
    }

    // 规则集
    private final Rule1AccompanyingDiseases rule1AccompanyingDiseases;
    private final Rule2AccompanyingSymptoms rule2AccompanyingSymptoms;
    private final Rule3DiseasesHasSuggestions rule3DiseasesHasSuggestions;
    private final Rule4SymptomsFindDiseases rule4SymptomsFindDiseases;
    // 制定规则集
    /**
     * 检查句子中是否存在（疾病/症状实体）
     * 1.疾病实体：
     *      1. 如果某种疾病存在伴随疾病，则搜索（疾病 + 伴随疾病）
     *      2. 疾病如果伴随某些症状，则搜索（疾病 + 症状）
     *      3. 如果疾病存在解决方案：药品，食物，菜谱
     * 2. 症状
     *      4. 如果包含多个症状，则症状的集合匹配是否存在疾病。
     */
    private List<Long> neo4jRuleSearch(List<PostNerResult> nerResults){
        List<Long> finalList = new ArrayList<>();
        List<String> diseaseNames = new ArrayList<>();
        for (PostNerResult nerResult : nerResults) {
            if (nerResult.getNerType().equals(DiseasesKnowledgeGraphEnum.DISEASES.getName())){
                diseaseNames.add(nerResult.getKeyWord());
            }
        }
        List<String> symptomNames = new ArrayList<>();
        for (PostNerResult nerResult : nerResults) {
            if (nerResult.getNerType().equals(DiseasesKnowledgeGraphEnum.SYMPTOMS.getName())){
                symptomNames.add(nerResult.getKeyWord());
            }
        }
        // 疾病：每个疾病都单独去查询
        for (String diseaseName : diseaseNames){
            List<Long> rule1MatchList = rule1AccompanyingDiseases.execute(diseaseName);
            List<Long> rule2MatchList = rule2AccompanyingSymptoms.execute(diseaseName);
            List<Long> rule3MatchList = rule3DiseasesHasSuggestions.execute(diseaseName);
            finalList.addAll(rule1MatchList);
            finalList.addAll(rule2MatchList);
            finalList.addAll(rule3MatchList);
        }
        // 症状：全部症状共同查询
        List<Long> rule4MatchList = rule4SymptomsFindDiseases.execute(symptomNames);
        finalList.addAll(rule4MatchList);
        // 去重
        return new ArrayList<>(new LinkedHashSet<>(finalList));
    }


    /**
     * 相似度查询
     * 疾病的Jacard相似度
     * 疾病之间的共同邻居相似度
     * 疾病之间的距离相似度
     */
    private List<Long> similaritySearch(List<PostNerResult> nerResults){
        List<String> diseaseNames = new ArrayList<>();
        for (PostNerResult nerResult : nerResults) {
            if (nerResult.getNerType().equals(DiseasesKnowledgeGraphEnum.DISEASES.getName())){
                diseaseNames.add(nerResult.getKeyWord());
            }
        }
        List<String> similarList = postSearchService.searchBySimilarity(diseaseNames, 3);
        List<Long> similarPostIds = new ArrayList<>();
        for (String similarName : similarList) {
            List<Long> postIds = postSearchService.searchPostIdsByLikeTitle(similarName);
            similarPostIds.addAll(postIds);
        }
        // 去重
        return new ArrayList<>(new LinkedHashSet<>(similarPostIds));
    }

    private List<PostInfoUrlAo> getPostInfoUrlAos(List<Long> postIds){
        List<PostInfoAo> postInfoAos = postSearchService.searchPostInfAoByIds(postIds);
        List<Long> fileIds = new ArrayList<>();
        for (PostInfoAo postInfoAo : postInfoAos){
            if (postInfoAo != null && !ObjectUtils.isEmpty(postInfoAo.getFileId())){
                fileIds.add(postInfoAo.getFileId());
            }
            else {
                // 为了保证返回顺序一一对应
                fileIds.add(null);
            }
        }
        List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
        List<PostInfoUrlAo> postInfoUrlAos = new ArrayList<>();
        assert fileUrls.size() == postInfoAos.size();
        for (int i = 0; i < postInfoAos.size(); i++){
            PostInfoAo postInfoAo = postInfoAos.get(i);
            PostInfoUrlAo postInfoUrlAo = postConverter.postInfoDoToUrlAo(postInfoAo);
            if (fileUrls.get(i) != null){
                postInfoUrlAo.setFileUrl(fileUrls.get(i));
            }
            else {
                postInfoUrlAo.setFileUrl(null);
            }
            postInfoUrlAos.add(postInfoUrlAo);
        }
        return postInfoUrlAos;
    }

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
