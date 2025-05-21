package com.offline.recommend.service.impl;

import com.czy.api.api.feature.UserFeatureService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.feature.FeatureTypeChanger;
import com.czy.api.constant.offline.OfflineConstant;
import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.domain.ao.feature.UserEntityScore;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.czy.api.mapper.DiseaseRepository;
import com.czy.api.mapper.UserFeatureRepository;
import com.offline.recommend.service.DistributedOfflineRecallCalculateService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/5/16 18:05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DistributedOfflineRecallCalculateServiceImpl implements DistributedOfflineRecallCalculateService {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserFeatureService userFeatureService;
    private final DiseaseRepository diseaseRepository;
    private final UserFeatureRepository userFeatureRepository;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    private final RedissonService redissonService;
    private final Environment environment;


    @Override
    public Map<Long, Double> getOfflineRecommend(Long userId) {
        // 召回：图召回
        Map<Long, Double> recallPostMap = graphRecall(userId);

        // 粗拍：特征截断
        Map<Long, Double> roughSortingPostMap = roughSorting(recallPostMap, userId);

        // 精排：负采样
        Map<Long, Double> fineSortingPostMap = fineSorting(roughSortingPostMap, userId);

        // 重排：热度 时间
        return reSorting(fineSortingPostMap);
    }


    /**
     * 图召回
     * 1.user画像构建：entity（itemCF：entity - post的映射关系 + [post分类标签]）
     * 2.知识工程：entity - relation - entity的映射关系
     * 3.图相似度：entity - similarEntity：相似实体映射关系 (此相似度存在偏差，最好只选用top2)
     * 4.user关系图：user和user的关系图谱 （最相似的好友，swing:user-entity/post-user趋同推荐）：关系userCF（不用user-CF，user-CF不稳定）
     * @param userId    用户id
     * @return            召回的entity集合
     */
    private Map<Long, Double> graphRecall(Long userId) {
        List<String> userGraphFeatureEntities = getUserGraphFeature(userId);

        if (CollectionUtils.isEmpty(userGraphFeatureEntities)){
            return new HashMap<>();
        }

        List<Long> result = new ArrayList<>();
        for (String userGraphFeatureEntity : userGraphFeatureEntities) {
            List<Long> postIds = postSearchService.searchPostIdsByLikeTitle(userGraphFeatureEntity);
            result.addAll(postIds);
        }

        // 重复内容double += 1.0
        Map<Long, Double> postIdScoreMap = result.stream()
                .collect(Collectors.toMap(postId -> postId, postId -> 1.0));

        // todo 去用户看过的重复内容

        return postIdScoreMap;
    }

    /**
     * 获取用户画像特征
     * @param userId    用户id
     * @return          用户画像特征集合
     */
    private List<String> getUserGraphFeature(Long userId){
        List<String> userFeatureEntities;

        // 1. user 画像构建 -> （带有权重的entity集合）
        List<UserEntityScore> userEntityScores = userFeatureService.getUserProfileList(userId);

        if (CollectionUtils.isEmpty(userEntityScores)){
            return new ArrayList<>();
        }

        // 取前三个，过滤entityType != NULL || POST_LABEL
        List<UserEntityScore> topKEntityScores = new ArrayList<>();
        for (int i = 0; i < Math.min(userEntityScores.size(), OfflineConstant.TOP_ENTITY_NUM); i++) {
            UserEntityScore userEntityScore = userEntityScores.get(i);
            if (userEntityScore.getEntityType() == DiseasesKnowledgeGraphEnum.NULL.getValue() ||
                    userEntityScore.getEntityType() == DiseasesKnowledgeGraphEnum.POST_LABEL.getValue()){
                continue;
            }
            topKEntityScores.add(userEntityScore);
        }
        if (CollectionUtils.isEmpty(topKEntityScores)){
            return new ArrayList<>();
        }

        // 拷贝名称
        userFeatureEntities = userEntityScores.stream()
                .map(UserEntityScore::getEntityName)
                .collect(Collectors.toList());

        // 2. 知识工程：(entity - relation - entity) -> 包含关联性(共同邻居)的entity集合 (暂略)
        // 3. 图相似度：entity - similarEntity -> 包含相似度的有序集合
        List<String> result = new ArrayList<>();
        for (UserEntityScore userEntityScore : topKEntityScores){
            String entityName = userEntityScore.getEntityName();
            Integer entityType = userEntityScore.getEntityType();
            if (DiseasesKnowledgeGraphEnum.DISEASES.getValue() == entityType){
                List<Map<String, Object>> jaccardResult = diseaseRepository.findTopSimilarDiseasesByJaccard(entityName, OfflineConstant.TOP_ENTITY_NUM);
                for (Map<String, Object> map : jaccardResult) {
                    String similarDiseaseName = (String) map.get("diseaseName");
                    result.add(similarDiseaseName);
                }
                List<Map<String, Object>> neighborResult = diseaseRepository.findTopSimilarDiseasesByNeighbor(entityName, OfflineConstant.TOP_ENTITY_NUM);
                for (Map<String, Object> map : neighborResult) {
                    String similarDiseaseName = (String) map.get("diseaseName");
                    result.add(similarDiseaseName);
                }
            }
            else {
                DiseasesKnowledgeGraphEnum enumByValue = DiseasesKnowledgeGraphEnum.getEnumByValue(entityType);
                String nodeLabel = FeatureTypeChanger.nodeLabelToRelation(enumByValue.getName());
                String relation = FeatureTypeChanger.getRelationCQL(nodeLabel);
                if (relation == null){
                    continue;
                }
                List<Map<String, Object>> similarEntities = userFeatureRepository.findTopSimilarByJaccard(
                        entityName, nodeLabel, relation, OfflineConstant.TOP_ENTITY_NUM
                );
                for (Map<String, Object> map : similarEntities) {
                    String similarEntityName = (String) map.get("similarEntityName");
                    result.add(similarEntityName);
                }
                List<Map<String, Object>> similarEntitiesByNeighbor = userFeatureRepository.findTopSimilarByNeighbor(
                        entityName, nodeLabel, relation, OfflineConstant.TOP_ENTITY_NUM
                );
                for (Map<String, Object> map : similarEntitiesByNeighbor) {
                    String similarEntityName = (String) map.get("similarEntityName");
                    result.add(similarEntityName);
                }
            }
        }

        userFeatureEntities.addAll(result);

        // 4. user关系图：swing图userCF -> 相似的user画像集合 -> （带有权重的entity集合）（暂略）

        return userFeatureEntities;
    }

    // 粗排
    private Map<Long, Double> roughSorting(Map<Long, Double> recallPostMap, Long userId){
        // todo 特征截断：swing；MF；FM
        return recallPostMap;
    }

    // 精排
    private Map<Long, Double> fineSorting(Map<Long, Double> roughPostIds, Long userId){
        // todo 负采样，点击预测，postLabels
        return roughPostIds;
    }

    // 重排
    private Map<Long, Double> reSorting(Map<Long, Double> finePostIds){
        // todo 根据时间 + 热度排序
        return finePostIds;
    }


    @Override
    public void allHeatUserOfflineRecommend() {
        log.info("开始尝试计算离线推荐列表, 当前时间：{}", LocalDateTime.now());

        // 获取活跃user；然后遍历，查询redis中是否存在数据，不存在就进行计算
        Collection<Object> activeUsers = redissonService.zReverseRange(
                OfflineRedisConstant.OFFLINE_USER_HEAT_KEY,
                0,
                -1
        );

        if (activeUsers.isEmpty()){
            log.warn("计算离线特征::没有活跃用户，结束计算");
            return;
        }

        ///  分布式计算
        for (Object activeUser : activeUsers){
            if (activeUser instanceof Long){
                Long userId = (Long) activeUser;

                String userRecommendKey = OfflineRedisConstant.USER_RECOMMEND_KEY + ":" + userId;
                
                if (redissonService.zCount(userRecommendKey) > FeatureConstant.USER_RECOMMEND_GET_NUM){
                    log.info("计算离线特征::用户{}的离线推荐列表已存在并大于{}，跳过计算", userId, FeatureConstant.USER_RECOMMEND_GET_NUM);
                    continue;
                }

                RedissonClusterLock redissonClusterLock =
                        new RedissonClusterLock(
                                userRecommendKey,
                                OfflineRedisConstant.USER_RECOMMEND_EXPIRE_TIME
                        );
                // 尝试获取分布式计算锁
                try {
                    if (redissonService.tryLock(redissonClusterLock)){ 
                        log.info("开始计算离线特征, 执行实例端口：{}，用户id：{}", getClusterCurrentPost(), userId);

                        Map<Long, Double> recommendPostIds = getOfflineRecommend(userId);
                        // 创建一个 Map 来存储 score 和 value
                        Map<Object, Double> recommendPostIdList = getListByMap(recommendPostIds);

                        if (!CollectionUtils.isEmpty(recommendPostIdList)){
                            // 存储到redis
                            redissonService.zAddAll(
                                    userRecommendKey,
                                    recommendPostIdList,
                                    OfflineRedisConstant.USER_RECOMMEND_EXPIRE_TIME
                            );
                        }
                    }
                } catch (Exception e){
                    log.error("计算离线特征::获取分布式锁失败，跳过计算");
                    continue;
                }
                finally {
                    // 解除分布式锁
                    redissonService.unlock(redissonClusterLock);
                }
            } 
        }
    }

    private String getClusterCurrentPost(){
        return environment.getProperty("server.port", "[unknow]");
    }

    public Map<Object, Double> getListByMap(Map<Long, Double> map){
        Map<Object, Double> zSet = new HashMap<>();
        for (Map.Entry<Long, Double> entry : map.entrySet()) {
            PostScoreAo innerMap = new PostScoreAo();
            innerMap.setPostId(entry.getKey());
            innerMap.setScore(entry.getValue());
            zSet.put(innerMap, entry.getValue());
        }
        return zSet;
    }
}
