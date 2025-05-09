package com.czy.search.service.impl;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.post.PostNerService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.search.rule.Rule1AccompanyingDiseases;
import com.czy.search.rule.Rule2AccompanyingSymptoms;
import com.czy.search.rule.Rule3DiseasesHasSuggestions;
import com.czy.search.rule.Rule4SymptomsFindDiseases;
import com.czy.search.service.FuzzySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/8 16:24
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FuzzySearchServiceImpl implements FuzzySearchService {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostNerService postNerService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final PostConverter postConverter;

    @Override
    public List<Long> likeSearch(String title) {
        List<Long> postIds = postSearchService.searchPostIdsByLikeTitle(title);
        if (postIds.isEmpty()){
            return new ArrayList<>();
        }
        // 去重
        return new ArrayList<>(new LinkedHashSet<>(postIds));
    }

    @Override
    public List<Long> tokenizedSearch(String title) {
        List<PostNerResult> nerResults = postNerService.getPostNerResults(title);
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
    @Override
    public List<Long> neo4jRuleSearch(List<PostNerResult> nerResults) {
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
    @Override
    public List<Long> similaritySearch(List<PostNerResult> nerResults) {
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

    @Override
    public List<PostInfoUrlAo> getPostInfoUrlAos(List<Long> postIds){
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
}
