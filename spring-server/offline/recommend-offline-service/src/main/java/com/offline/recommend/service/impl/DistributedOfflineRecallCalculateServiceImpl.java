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

    @Override
    public List<Long> graphRecall(Long userId) {
        // 路径1就是临时特征，不参与
        // 共同邻居：user->entity：对不同的entity单独写mapper函数，因为每个entity的关系不同。主要关心的是：疾病，症状，药品，食物，菜谱 TODO 存在问题，user-entity的关系本来就很少交集，共同邻居应该是相同的实体
        // Jaacard相似度
        // 了解更多的图相似度计算
        return null;
    }
}
