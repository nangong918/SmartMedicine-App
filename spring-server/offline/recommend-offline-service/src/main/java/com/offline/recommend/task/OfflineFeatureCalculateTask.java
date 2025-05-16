package com.offline.recommend.task;

import com.czy.api.api.feature.UserHeatService;
import com.czy.api.domain.ao.feature.UserHeatAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/15 16:29
 */

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class OfflineFeatureCalculateTask {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserHeatService userHeatService;

    // 每天晚上凌晨2点开始特征计算[cron表达式]
    private static final String CRON_EXPRESSION = "0 0 2 * * ?";

    @Scheduled(cron = CRON_EXPRESSION)
    public void offlineFeatureCalculate() {
        log.info("开始计算离线特征, 当前时间：{}", LocalDateTime.now());
        long userHeatCalculateStartTime = System.currentTimeMillis();
        List<UserHeatAo> userHeatAos = userHeatService.getUsersHeat();
        long userHeatCalculateEndTime = System.currentTimeMillis();
        log.info("计算用户热度完成，耗时：{}ms, 当前时间：{}", userHeatCalculateEndTime - userHeatCalculateStartTime, LocalDateTime.now());
    }

}
