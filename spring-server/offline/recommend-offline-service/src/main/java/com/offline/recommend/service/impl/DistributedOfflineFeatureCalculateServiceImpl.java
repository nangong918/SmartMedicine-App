package com.offline.recommend.service.impl;

import com.czy.api.api.feature.UserFeatureService;
import com.czy.api.api.feature.UserHeatService;
import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.domain.ao.feature.UserTempFeatureAo;
import com.czy.api.domain.ao.feature.UserHeatAo;
import com.offline.recommend.service.DistributedOfflineFeatureCalculateService;
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

/**
 * @author 13225
 * @date 2025/5/16 15:11
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DistributedOfflineFeatureCalculateServiceImpl implements DistributedOfflineFeatureCalculateService {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserHeatService userHeatService;
    private final RedissonService redissonService;
    private final Environment environment;
    private final UserFeatureService userFeatureService;

    @Override
    public void calculateUserHeat() {
        // 检查是否已经开始计算了：并且不主动解除分布式锁，因为一天只算一次
        RedissonClusterLock redissonClusterLock =
                new RedissonClusterLock(
                        OfflineRedisConstant.OFFLINE_USER_HEAT_CALCULATE_FLAG,
                        OfflineRedisConstant.offlineUserHeatCalculateFlagExpireTime
                );

        if (redissonService.tryLock(redissonClusterLock)){
            log.info("开始计算用户热度, 执行端口号：{}", getClusterCurrentPost());

            List<UserHeatAo> userHeatAos = getUserHeatAos();
            if (CollectionUtils.isEmpty(userHeatAos)){
                log.warn("用户热度列表为空，结束计算");
                return;
            }

            // 转为ZSet的键值对
            Map<Object, Double> zSetValues = new HashMap<>();
            for (UserHeatAo userHeatAo : userHeatAos) {
                // 假设 userId 是唯一标识，热度可以是某个属性，比如热度值
                zSetValues.put(
                        userHeatAo.getUserId(),
                        userHeatAo.getHeatScore()
                );
            }

            // 存储到redis
            redissonService.zAddAll(
                    OfflineRedisConstant.OFFLINE_USER_HEAT_KEY,
                    zSetValues,
                    OfflineRedisConstant.ONE_DAY
            );
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

    @Override
    public void calculateUserFeatures() {
        log.info("开始尝试计算离线特征, 当前时间：{}", LocalDateTime.now());

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
        for (Object activeUser : activeUsers) {
            if (activeUser instanceof Long){
                Long userId = (Long) activeUser;

                // Map<UserId(str), UserTempFeatureAo>
                Map<String, Object> userHistoryFeatureMap = redissonService.getObjectHaseMap(
                        OfflineRedisConstant.USER_HISTORY_FEATURE_KEY
                );

                boolean isExecute = CollectionUtils.isEmpty(userHistoryFeatureMap) ||
                        userHistoryFeatureMap.get(String.valueOf(userId)) == null;

                // 不执行的情况
                if (!isExecute){
                    continue;
                }

                RedissonClusterLock redissonClusterLock =
                        new RedissonClusterLock(
                                String.valueOf(userId),
                                OfflineRedisConstant.USER_HISTORY_FEATURE_KEY,
                                OfflineRedisConstant.USER_HISTORY_FEATURE_EXPIRE_TIME
                        );
                // 尝试获取分布式计算锁
                try {
                    if (redissonService.tryLock(redissonClusterLock)){
                        log.info("开始计算离线特征, 执行实例端口：{}，用户id：{}", getClusterCurrentPost(), userId);
                        // userId -> userFeature
                        UserTempFeatureAo userTempFeatureAo = userFeatureService.getUserTempFeature(userId);
                        if (CollectionUtils.isEmpty(userHistoryFeatureMap)){
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(String.valueOf(userId), userTempFeatureAo);
                            redissonService.saveObjectHashMap(
                                    OfflineRedisConstant.USER_HISTORY_FEATURE_KEY,
                                    hashMap,
                                    OfflineRedisConstant.USER_HISTORY_FEATURE_EXPIRE_TIME
                            );
                        }
                        else {
                            redissonService.updateObjectHashMap(
                                    OfflineRedisConstant.USER_HISTORY_FEATURE_KEY,
                                    String.valueOf(userId),
                                    userTempFeatureAo
                            );
                        }
                    }
                } catch (Exception e){
                    log.error("分布式计算user特征异常，userId：{}", userId, e);
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
}
