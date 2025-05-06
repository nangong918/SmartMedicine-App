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
        // 基于内容的问题，存在特征不清晰的情况，就比方说，如果帖子基本全都是非专业属于的分享帖子，标签大量标签相同，属于是特征工程做的不到位。系统中不应该存在大量特征相同的。
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



}
