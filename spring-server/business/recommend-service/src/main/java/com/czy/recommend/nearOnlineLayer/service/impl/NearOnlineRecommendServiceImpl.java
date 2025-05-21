package com.czy.recommend.nearOnlineLayer.service.impl;

import com.czy.api.api.feature.FeatureRuleService;
import com.czy.api.api.feature.PostFeatureService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.domain.ao.feature.FeatureContext;
import com.czy.api.domain.ao.feature.NerFeatureScoreAo;
import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.feature.ScoreAo;
import com.czy.api.domain.ao.feature.UserTempFeatureAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.czy.recommend.nearOnlineLayer.service.NearOnlineRecommendService;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/5/20 15:38
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NearOnlineRecommendServiceImpl implements NearOnlineRecommendService {

    private final RedissonService redissonService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private FeatureRuleService featureRuleService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostFeatureService postFeatureService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;

    @Override
    public List<PostScoreAo> getNearOnlineRecommend(FeatureContext context) {
        Object userHistoryFeature = redissonService.getObjectFromHashMap(
                OfflineRedisConstant.USER_HISTORY_FEATURE_KEY,
                String.valueOf(context.getUserId())
        );
        if (userHistoryFeature == null){
            return new ArrayList<>();
        }

        if (userHistoryFeature instanceof UserTempFeatureAo){
            return getNearOnlineRecommend((UserTempFeatureAo) userHistoryFeature);
        }

        return new ArrayList<>();
    }

    private List<PostScoreAo> getNearOnlineRecommend(UserTempFeatureAo userTempFeatureAo){
        Map<String, Double> map = getEntityScoreMapByUserTempFeatureAo(userTempFeatureAo);

        // map -> list<Map<String, Double>> 根据分数排序
        List<Map<String, Double>> list = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Double::compareTo))
                .map(Map.Entry::getKey)
                .map(key -> {
                    Map<String, Double> innerMap = new HashMap<>();
                    innerMap.put(key, map.get(key));
                    return innerMap;
                }).collect(Collectors.toList());

        List<PostScoreAo> postScoreAoList = new ArrayList<>();

        for (Map<String, Double> entity : list){
            for (Map.Entry<String, Double> entry : entity.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                PostScoreAo postScoreAo = new PostScoreAo();
                postScoreAo.setPostId(Long.parseLong(key));
                postScoreAo.setScore(value);
                postScoreAoList.add(postScoreAo);
            }
        }

        // label分数
        Map<Integer, ScoreAo> labelScoreAoMap = userTempFeatureAo.getLabelScoreMap();
        Map<Integer, Double> labelScoreMap = new HashMap<>();
        for (Map.Entry<Integer, ScoreAo> entry : labelScoreAoMap.entrySet()) {
            Integer label = entry.getKey();
            ScoreAo scoreAo = entry.getValue();
            labelScoreMap.put(label, featureRuleService.scoreAoToScore(scoreAo));
        }
        // TODO 用 labelScoreMap 重新排序postId
        return postScoreAoList;
    }


    /**
     * 根据用户临时特征获取实体分数
     * @param userTempFeatureAo 用户临时特征
     * @return                  实体分数
     */
    private Map<String, Double> getEntityScoreMapByUserTempFeatureAo(UserTempFeatureAo userTempFeatureAo){
        Map<String, Double> entityFinalScoreMap = new HashMap<>();
        Map<Long, Double> postScoreMap = getPostScoreMap(userTempFeatureAo);
        Map<String, Double> postEntityScoreMap = postScoreMapToEntityScoreMap(postScoreMap);
        Map<String, Double> entityScoreMap = getEntityScoreMap(userTempFeatureAo);
        entityFinalScoreMap = addMap(entityFinalScoreMap, postEntityScoreMap);
        entityFinalScoreMap = addMap(entityFinalScoreMap, entityScoreMap);
        return entityFinalScoreMap;
    }


    /**
     * 获取帖子分数
     * @param userTempFeatureAo 用户临时特征
     * @return                  帖子分数
     */
    private Map<Long, Double> getPostScoreMap(UserTempFeatureAo userTempFeatureAo){
        Map<Long, Double> postScoreMap = new HashMap<>();
        Map<Long, ScoreAo> postScoreAoMap = userTempFeatureAo.getPostScoreMap();
        for (Map.Entry<Long, ScoreAo> entry : postScoreAoMap.entrySet()) {
            Double score = featureRuleService.scoreAoToScore(entry.getValue());
            postScoreMap.put(entry.getKey(), score);
        }
        return postScoreMap;
    }

    /**
     * 帖子分数转实体分数
     * @param postScoreMap  帖子分数
     * @return              实体分数
     */
    private Map<String, Double> postScoreMapToEntityScoreMap(Map<Long, Double> postScoreMap) {
        Map<String, Double> entityScoreMap = new HashMap<>();
        for (Map.Entry<Long, Double> entity : postScoreMap.entrySet()){
            Long postId = entity.getKey();
            PostFeatureAo postFeatureAo = postFeatureService.getPostFeature(postId);
            Double score = entity.getValue();
            for (PostNerResult postNerResult : postFeatureAo.getPostNerResultList()){
                String keyWord = postNerResult.getKeyWord();
                entityScoreMap.put(keyWord, score);
            }
        }
        return entityScoreMap;
    }

    /**
     * 获取实体分数
     * @param userTempFeatureAo 用户临时特征
     * @return                  实体分数
     */
    private Map<String, Double> getEntityScoreMap(UserTempFeatureAo userTempFeatureAo) {
        Map<String, Double> entityScoreMap = new HashMap<>();
        Map<String, NerFeatureScoreAo> entityScoreAoMap = userTempFeatureAo.getNerFeatureScoreMap();
        for (Map.Entry<String, NerFeatureScoreAo> entry : entityScoreAoMap.entrySet()) {
            Double score = featureRuleService.scoreAoToScore(entry.getValue().getScore());
            entityScoreMap.put(entry.getKey(), score);
        }
        return entityScoreMap;
    }

    // 叠加Map<String, Double>
    private Map<String, Double> addMap(Map<String, Double> map1, Map<String, Double> map2) {
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : map1.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            result.put(key, value);
        }
        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            result.merge(key, value, Double::sum);
        }
        return result;
    }
}
