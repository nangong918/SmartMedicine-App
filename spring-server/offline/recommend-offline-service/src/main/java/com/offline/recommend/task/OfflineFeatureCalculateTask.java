package com.offline.recommend.task;

import com.offline.recommend.service.DistributedOfflineFeatureCalculateService;
import com.offline.recommend.service.DistributedOfflineRecallCalculateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/15 16:29
 */

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class OfflineFeatureCalculateTask {

    private final DistributedOfflineFeatureCalculateService distributedOfflineFeatureCalculateService;
    private final DistributedOfflineRecallCalculateService distributedOfflineRecallCalculateService;

    // 每天晚上凌晨1点开始计算用户热[cron表达式]
    private static final String USEE_HEAT_EXPRESSION = "0 0 1 * * ?";
    @Scheduled(cron = USEE_HEAT_EXPRESSION)
    public void offlineUserHeatCalculate() {
        distributedOfflineFeatureCalculateService.calculateUserHeat();
    }

    // 每天晚上凌晨2点开始特征计算[cron表达式]
    private static final String FEATURE_EXPRESSION = "0 0 2 * * ?";

    @Scheduled(cron = FEATURE_EXPRESSION)
    public void offlineFeatureCalculate() {
        distributedOfflineFeatureCalculateService.calculateUserFeatures();
    }

    // 每天晚上凌晨2点开始计算计算用户召回队列
    private static final String RECALL_EXPRESSION = "0 0 2 * * ?";
    @Scheduled(cron = RECALL_EXPRESSION)
    public void offlineRecallCalculate() {
        distributedOfflineRecallCalculateService.allHeatUserOfflineRecommend();
    }
}
