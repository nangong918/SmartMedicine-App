package com.czy.search.controller;

import com.czy.api.api.post.PostNerService;
import com.czy.api.constant.search.FuzzySearchResponseEnum;
import com.czy.api.constant.search.SearchConstant;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.ao.search.PostSearchResultAo;
import com.czy.api.domain.dto.http.request.FuzzySearchRequest;
import com.czy.api.domain.dto.http.response.FuzzySearchResponse;
import com.czy.api.domain.dto.python.NlpSearchResponse;
import com.czy.search.service.FuzzySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private PostNerService postNerService;
    private final FuzzySearchService fuzzySearchService;
    private final RestTemplate restTemplate;
    /**
     * 模糊搜索
     * @param request  模糊搜索的句子 + userId（用于userContext特征上下文）
     * @return  搜索结果
     */
    @PostMapping("/fuzzy")
    public FuzzySearchResponse fuzzySearch(@Valid @RequestBody FuzzySearchRequest request) {
        // 搜索这种消耗资源的操作需要给用户上限流和分布式锁
        FuzzySearchResponse response = new FuzzySearchResponse();

        // 提取搜素句子
        String sentence = request.getSentence();

        // python服务处理nlp搜索

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", sentence);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        // python搜索响应
        ResponseEntity<NlpSearchResponse> pythonResponseEntity = restTemplate.postForEntity(
                SearchConstant.PYTHON_NLP_SEARCH_URL,
                requestEntity,
                NlpSearchResponse.class
        );
        // 处理 Python 响应
        NlpSearchResponse nlpSearchResponse = null;
        if (pythonResponseEntity.getStatusCode().is2xxSuccessful()) {
            nlpSearchResponse = pythonResponseEntity.getBody();
        }
        else {
            response.setType(FuzzySearchResponseEnum.NO_RESULT.getType());
        }
        if (nlpSearchResponse == null || nlpSearchResponse.getCode() != 200){
            response.setType(FuzzySearchResponseEnum.ERROR_RESULT.getType());
            return response;
        }

        // 到此处了说明一定是post
        response.setType(FuzzySearchResponseEnum.SEARCH_POST_RESULT.getType());
        PostSearchResultAo postSearchResultAo = new PostSearchResultAo();

        // 0~1级搜索 到此处说明sentence本身就是title，所以likeTitle传递sentence;
        List<Long> likePostIdList = fuzzySearchService.likeSearch(sentence);
        // 2级搜索 搜索：AcTree匹配实体 + ElasticSearch搜索;
        // 缓存结结果，避免后续搜索调用两次
        List<PostNerResult> nerResults = postNerService.getPostNerResults(sentence);
        List<Long> tokenizedPostIdList = fuzzySearchService.tokenizedSearch(sentence);


        // 3级搜索：neo4j规则集 + es查询 + user context vector排序;
        List<Long> neo4jRulePostIdList = fuzzySearchService.neo4jRuleSearch(nerResults);
        // 4级搜索：neo4j疾病相似度查询 + user context vector排序;
        List<Long> similarList = fuzzySearchService.similaritySearch(nerResults);

        // 转换
        postSearchResultAo.setLikePostList(fuzzySearchService.getPostInfoUrlAos(likePostIdList));
        postSearchResultAo.setTokenizedPostList(fuzzySearchService.getPostInfoUrlAos(tokenizedPostIdList));
        postSearchResultAo.setSimilarPostList(fuzzySearchService.getPostInfoUrlAos(similarList));
        postSearchResultAo.setRecommendPostList(fuzzySearchService.getPostInfoUrlAos(neo4jRulePostIdList));

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


    // TODO 用python给IK导出一份词典！！！！！！！！！！！！！！！！！！！！！！！！


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
