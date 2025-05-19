package com.offline.recommend.service.impl;

import com.czy.api.api.feature.UserFeatureService;
import com.czy.api.constant.feature.FeatureTypeChanger;
import com.czy.api.constant.offline.OfflineConstant;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.domain.ao.feature.UserEntityScore;
import com.czy.api.mapper.DiseaseRepository;
import com.czy.api.mapper.UserFeatureRepository;
import com.offline.recommend.service.DistributedOfflineRecallCalculateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    /**
     * 图召回
     * 1.user画像构建：entity（itemCF：entity - post的映射关系 + [post分类标签]）
     * 2.知识工程：entity - relation - entity的映射关系
     * 3.图相似度：entity - similarEntity：相似实体映射关系 (此相似度存在偏差，最好只选用top2)
     * 4.user关系图：user和user的关系图谱 （最相似的好友，swing:user-entity/post-user趋同推荐）：关系userCF（不用user-CF，user-CF不稳定）
     * @param userId    用户id
     * @return            召回的entity集合
     */
    @Override
    public List<Long> graphRecall(Long userId) {
        List<String> userFeatureEntities = getUserFeatureEntities(userId);
        // TODO List<String> 查询对应文章，然后召回；粗拍，精排；重排
        return null;
    }

    private List<String> getUserFeatureEntities(Long userId){
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
}
