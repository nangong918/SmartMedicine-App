package com.offline.recommend.service.impl;

import com.czy.api.mapper.UserFeatureRepository;
import com.offline.recommend.service.DistributedOfflineRecallCalculateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 18:05
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DistributedOfflineRecallCalculateServiceImpl implements DistributedOfflineRecallCalculateService {

    private final UserFeatureRepository userFeatureRepository;

    /**
     * 图召回
     * 1.user画像构建：entity（itemCF：entity - post的映射关系 + [post分类标签]）
     * 2.知识工程：entity - relation - entity的映射关系
     * 3.图相似度：entity - similarEntity：相似实体映射关系 (此相似度存在偏差，最好只选用top2)
     * 4.user关系图：user和user的关系图谱 （最相似的好友，swing:user-entity/post-user趋同推荐）：关系userCF（不用user-CF，user-CF不稳定）
     * @param userId
     * @return
     */
    @Override
    public List<Long> graphRecall(Long userId) {
        // 1. user 画像构建 -> （带有权重的entity集合）

        // 2. 知识工程：(entity - relation - entity) -> 包含关联性(共同邻居)的entity集合

        // 3. 图相似度：entity - similarEntity -> 包含相似度的有序集合

        // 4. user关系图：swing图userCF -> 相似的user画像集合 -> （带有权重的entity集合）
        return null;
    }
}
