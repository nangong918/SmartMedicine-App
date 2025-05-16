package com.offline.recommend.task;

import com.czy.api.api.feature.UserHeatService;
import com.czy.api.domain.ao.feature.UserHeatAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        List<UserHeatAo> userHeatAos = getUserHeatAos();
        if (CollectionUtils.isEmpty(userHeatAos)){
            log.warn("用户热度列表为空，结束计算");
            return;
        }

    }

    private List<UserHeatAo> getUserHeatAos() {
        long userHeatCalculateStartTime = System.currentTimeMillis();
        List<UserHeatAo> userHeatAos = userHeatService.getUsersHeat();
        if (CollectionUtils.isEmpty(userHeatAos)){
            return new ArrayList<>();
        }
        else {
            log.info("用户数量：{}", userHeatAos.size());
        }
        // 按照热度从大到校排序 + 排序时间
        log.info("开始排序用户热度列表");
        long userHeatSortStartTime = System.currentTimeMillis();
        userHeatAos.sort((o1, o2) -> o2.getHeatScore().compareTo(o1.getHeatScore()));
        // 输出排序时间和计算时间
        log.info("排序耗时：{}ms， 计算总耗时：{}ms",
                System.currentTimeMillis() - userHeatSortStartTime,
                System.currentTimeMillis() - userHeatCalculateStartTime);
        return userHeatAos;
    }

}
