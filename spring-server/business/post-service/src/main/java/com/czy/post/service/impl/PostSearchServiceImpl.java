package com.czy.post.service.impl;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.es.FieldAnalyzer;
import com.czy.api.constant.search.SearchConstant;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import com.czy.api.domain.Do.post.post.PostFilesDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.ao.post.PostSearchEsAo;
import com.czy.api.domain.ao.user.AuthorAo;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.domain.vo.post.PostVo;
import com.czy.api.mapper.DiseaseRepository;
import com.czy.post.front.PostFrontService;
import com.czy.post.mapper.mongo.PostDetailMongoMapper;
import com.czy.post.mapper.mysql.PostFilesMapper;
import com.czy.post.mapper.mysql.PostInfoMapper;
import com.czy.post.service.PostStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/5/6 11:47
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class PostSearchServiceImpl implements PostSearchService {

    private final PostInfoMapper postInfoMapper;
    private final DiseaseRepository diseaseRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final PostConverter postConverter;
    private final PostFilesMapper postFilesMapper;
    private static final String SEARCH_KEY_ATTRIBUTE = "title";
    // elasticsearch的客户端
    private final RestHighLevelClient restHighLevelClient;
    private final PostDetailMongoMapper postDetailMongoMapper;
    private final PostStorageService postStorageService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final PostFrontService postFrontService;

    @Override
    public List<Long> searchPostIdsByLikeTitle(String likeTitle) {
        if (!StringUtils.hasText(likeTitle)){
            return new ArrayList<>();
        }
        List<Long> postIds = postInfoMapper.findPostIdByLikeTitle(likeTitle);
        if (CollectionUtils.isEmpty(postIds)){
            return new ArrayList<>();
        }
        return postIds;
    }

    @Override
    public List<Long> searchPostIdsByTokenizedTitle(String tokenizedTitle) {
        if (!StringUtils.hasText(tokenizedTitle)){
            return new ArrayList<>();
        }
        try {
            List<String> filteredTokens = segmentWord(tokenizedTitle);
            if (filteredTokens.size() < 2){
                log.warn("tokenizedSearch::分词结果太少：词典关键词数量小于2，返回空");
                return new ArrayList<>();
            }
            List<PostSearchEsAo> postSearchEsAoList = searchByKeywords(filteredTokens);
            return getPostIds(postSearchEsAoList);
        } catch (IOException e) {
            log.error("tokenizedSearch::分词失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<PostSearchEsAo> searchByKeywords(List<String> keywords) {
        return searchByKeywordsByMapper(keywords, SearchConstant.SEARCH_MIN_WORLDS);
    }

    @Override
    public List<PostSearchEsAo> searchByKeywordsByMapper(List<String> keywords, int minShouldMatch) {
        if (CollectionUtils.isEmpty(keywords)) {
            return Collections.emptyList();
        }

        int maxKeyWorlds = (int)(SearchConstant.SEARCH_MAX_WORLDS / 2);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加所有关键词作为should条件
        keywords.stream()
                .limit(maxKeyWorlds) // 限制最大关键词数量
                .forEach(keyword ->
                        boolQuery.should(QueryBuilders.matchQuery(
                                SEARCH_KEY_ATTRIBUTE,
                                keyword))
                );

        // 设置最小匹配数
        boolQuery.minimumShouldMatch(minShouldMatch);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();

        List<PostDetailEsDo> postDetailEsDoList = elasticsearchRestTemplate.search(searchQuery, PostDetailEsDo.class)
                .get()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        if (postDetailEsDoList.isEmpty()){
            return new ArrayList<>();
        }

        // 结果按照匹配到词语的数量进行排序
        // 转换结果并计算匹配数量
        return postDetailEsDoList.stream()
                .map(esDo -> {
                    PostSearchEsAo ao = new PostSearchEsAo();
                    ao.setPostDetailEsDo(esDo);
                    ao.setMatchedCount(calculateMatchedCount(esDo.getTitle(), keywords));
                    return ao;
                })
                .sorted((a, b) -> b.getMatchedCount() - a.getMatchedCount()) // 降序排序
                .collect(Collectors.toList());
    }

    @Override
    public List<String> searchBySimilarity(List<String> diseaseNames, int limitNum) {
        List<String> result = new ArrayList<>();
        for (String diseaseName : diseaseNames){
            List<Map<String, Object>> jaccardResult = diseaseRepository.findTopSimilarDiseasesByJaccard(diseaseName, limitNum);
            for (Map<String, Object> map : jaccardResult) {
                String similarDiseaseName = (String) map.get("diseaseName");
                result.add(similarDiseaseName);
            }
            List<Map<String, Object>> neighborResult = diseaseRepository.findTopSimilarDiseasesByNeighbor(diseaseName, limitNum);
            for (Map<String, Object> map : neighborResult) {
                String similarDiseaseName = (String) map.get("diseaseName");
                result.add(similarDiseaseName);
            }
            List<Map<String, Object>> pathResult = diseaseRepository.findTopSimilarDiseasesByPath1(diseaseName, limitNum);
            for (Map<String, Object> map : pathResult) {
                String similarDiseaseName = (String) map.get("diseaseName");
                result.add(similarDiseaseName);
            }
        }
        return result;
    }

    @Override
    public List<PostInfoAo> searchPostInfAoByIds(List<Long> postIds) {
        List<PostInfoAo> postInfoAos = new ArrayList<>();
        List<PostInfoDo> postInfoDoList = postInfoMapper.getPostInfoDoListByIdList(postIds);
        for (PostInfoDo postInfoDo : postInfoDoList) {
            List<PostFilesDo> postFilesDoList = postFilesMapper.getPostFilesDoListByPostId(postInfoDo.getId());
            PostInfoAo postInfoAo = postConverter.postInfoDoToAo(postInfoDo);
            if (!CollectionUtils.isEmpty(postFilesDoList)){
                PostFilesDo postFilesDo = postFilesDoList.get(0);
                Long fileId = postFilesDo.getFileId();
                postInfoAo.setFileId(fileId);
            }
            postInfoAos.add(postInfoAo);
        }
        return postInfoAos;
    }

    @Override
    public PostDetailDo searchPostDetailById(Long postId) {
        return postDetailMongoMapper.findPostDetailById(postId);
    }

    private int calculateMatchedCount(String content, List<String> keywords) {
        if (StringUtils.isEmpty(content) || CollectionUtils.isEmpty(keywords)) {
            return 0;
        }

        int count = 0;
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                count++;
            }
        }
        return count;
    }

    private List<String> segmentWord(String message) throws IOException {
        AnalyzeRequest request = AnalyzeRequest.withGlobalAnalyzer(
                FieldAnalyzer.IK_MAX_WORD,
                message
        );

        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();

        return tokens.stream()
                .map(AnalyzeResponse.AnalyzeToken::getTerm) // 获取分词结果
                // 过滤掉长度小于等于2的词
                // 新冠 -> 新型冠状  （单字情况不考虑，太消耗cpu资源了）
                .filter(term -> term.length() >= 2)
                .collect(Collectors.toList());
    }

    // list<PostEsAo> -> list<Long> ids
    private List<Long> getPostIds(List<PostSearchEsAo> postSearchEsAoList) {
        if (CollectionUtils.isEmpty(postSearchEsAoList)) {
            return new ArrayList<>();
        }
        List<PostSearchEsAo> sortList = postSearchEsAoList.stream()
                .sorted((a, b) -> b.getMatchedCount() - a.getMatchedCount())
                .collect(Collectors.toList());
        return sortList.stream()
                .map(PostSearchEsAo::getPostDetailEsDo)
                .map(PostDetailEsDo::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostInfoAo> findPostInfoList(List<Long> idList) {
        return postStorageService.findPostInfoAoList(idList);
    }

    @Override
    public List<PostInfoUrlAo> getPostInfoUrlAos(List<Long> postIds){
        List<PostInfoAo> postInfoAos = searchPostInfAoByIds(postIds);
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
        List<Long> authorIds = new ArrayList<>();
        for (PostInfoAo postInfoAo : postInfoAos){
            if (postInfoAo != null && !ObjectUtils.isEmpty(postInfoAo.getAuthorId())){
                authorIds.add(postInfoAo.getAuthorId());
            }
            else {
                // 为了保证返回顺序一一对应
                authorIds.add(null);
            }
        }
        List<UserDo> userDos = userService.getByUserIdsWithNull(authorIds);
        List<Long> authorFileIds = new ArrayList<>();
        for (UserDo userDo : userDos){
            if (userDo == null){
                authorFileIds.add(null);
                continue;
            }
            authorFileIds.add(userDo.getAvatarFileId());
        }
        List<String> authorUrlList = ossService.getFileUrlsByFileIds(authorFileIds);
        List<AuthorAo> authorAos = new ArrayList<>();
        for (int i = 0; i < userDos.size(); i++){
            UserDo userDo = userDos.get(i);
            if (userDo == null){
                authorAos.add(null);
                continue;
            }
            AuthorAo authorAo = new AuthorAo();
            authorAo.setAuthorName(userDo.getUserName());
            authorAo.setAuthorAvatarUrl(authorUrlList.get(i));
            authorAos.add(authorAo);
        }
        List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);
        List<PostInfoUrlAo> postInfoUrlAos = new ArrayList<>();
        assert fileUrls.size() == postInfoAos.size();
        for (int i = 0; i < postInfoAos.size(); i++){
            PostInfoAo postInfoAo = postInfoAos.get(i);
            // TODO 阅读量需要从Redis和MySQL拿取
            PostInfoUrlAo postInfoUrlAo = postConverter.postInfoDoToUrlAo(postInfoAo);
            if (authorAos.get(i) != null){
                postInfoUrlAo.setAuthorName(authorAos.get(i).getAuthorName());
                postInfoUrlAo.setAuthorAvatarUrl(authorAos.get(i).getAuthorAvatarUrl());
            }
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

    @Override
    public List<PostPreviewVo> getPostPreviewVosByIds(List<Long> postIds) {
        if (CollectionUtils.isEmpty(postIds)){
            return new ArrayList<>();
        }
        List<PostInfoAo> postInfoAos = findPostInfoList(postIds);
        if (CollectionUtils.isEmpty(postInfoAos)){
            return new ArrayList<>();
        }
        return postFrontService.toPostPreviewVoList(postInfoAos);
    }

    @Override
    public PostVo getPostVoById(Long postId) {
        if (postId == null){
            return null;
        }
        PostAo postAo = postStorageService.findPostAoById(postId);
        if (postAo == null || postAo.getId() == null){
            return null;
        }
        return postFrontService.postAoToPostVo(postAo);
    }
}
