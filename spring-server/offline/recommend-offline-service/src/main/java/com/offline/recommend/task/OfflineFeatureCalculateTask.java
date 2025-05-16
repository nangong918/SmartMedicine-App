package com.offline.recommend.task;

import com.czy.api.api.feature.UserHeatService;
import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.domain.ao.feature.UserHeatAo;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final RedissonService redissonService;
    private final Environment environment;

    // 每天晚上凌晨1点开始计算用户热[cron表达式]
    private static final String USEE_HEAT_EXPRESSION = "0 0 1 * * ?";
    public void offlineUserHeatCalculate() {
        // 检查是否已经开始计算了：并且不主动解除分布式锁，因为一天只算一次
        RedissonClusterLock redissonClusterLock =
                new RedissonClusterLock(
                        OfflineRedisConstant.OFFLINE_USER_HEAT_CALCULATE_FLAG,
                        OfflineRedisConstant.offlineUserHeatCalculateFlagExpireTime
                );

        String port = environment.getProperty("server.port", "[unknow]");
        if (redissonService.tryLock(redissonClusterLock)){
            log.info("开始计算用户热度, 执行端口号：{}", port);

            List<UserHeatAo> userHeatAos = getUserHeatAos();
            if (CollectionUtils.isEmpty(userHeatAos)){
                log.warn("用户热度列表为空，结束计算");
                return;
            }

            // 转为ZSet的键值对
            Map<Object, Double> zSetValues = new HashMap<>();
            for (UserHeatAo userHeatAo : userHeatAos) {
                // 假设 userId 是唯一标识，热度可以是某个属性，比如热度值
                zSetValues.put(userHeatAo.getUserId(), userHeatAo.getHeatScore()); // 需要确保有 getHeatValue 方法
            }

            // 存储到redis
            redissonService.zAddAll(
                    OfflineRedisConstant.OFFLINE_USER_HEAT_KEY,
                    zSetValues,
                    OfflineRedisConstant.ONE_DAY
            );
        }
    }

    // 每天晚上凌晨2点开始特征计算[cron表达式]
    private static final String FEATURE_EXPRESSION = "0 0 2 * * ?";

    @Scheduled(cron = FEATURE_EXPRESSION)
    public void offlineFeatureCalculate() {
        log.info("开始计算离线特征, 当前时间：{}", LocalDateTime.now());
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
