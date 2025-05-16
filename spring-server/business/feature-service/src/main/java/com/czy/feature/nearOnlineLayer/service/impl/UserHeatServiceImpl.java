package com.czy.feature.nearOnlineLayer.service.impl;

import com.czy.api.api.feature.UserHeatService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.feature.UserActionRedisKey;
import com.czy.api.domain.ao.feature.HeatDaysAo;
import com.czy.api.domain.ao.feature.UserHeatAo;
import com.czy.api.domain.ao.feature.UserHeatRecordAo;
import com.czy.feature.nearOnlineLayer.rule.RuleTempFeature;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 11:05
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class UserHeatServiceImpl implements UserHeatService {

    private final RedissonService redissonService;
    private final RuleTempFeature ruleTempFeature;


    @Override
    public UserHeatAo getUserHeat(Long userId) {
        // 1.计算时间戳
        long currentTime = System.currentTimeMillis();
        // 30天前的时间戳
        long thirtyDaysAgoTime = currentTime - FeatureConstant.FEATURE_EXPIRE_TIME_SECOND * 1000L;

        String userHeatKey = UserActionRedisKey.USER_HEAT_REDIS_KEY + userId;
        Collection<Object> userHeatRecordAos = redissonService.zRangeByScore(
                userHeatKey,
                (double) thirtyDaysAgoTime,
                (double) currentTime);
        UserHeatAo userHeatAo = getUserHeatAo(userHeatRecordAos);
        userHeatAo.setUserId(userId);
        return userHeatAo;
    }

    private UserHeatAo getUserHeatAo(Collection<Object> list) {
        UserHeatAo userHeatAo = new UserHeatAo();
        if (CollectionUtils.isEmpty(list)){
            userHeatAo.setHeatScore(0.0);
            return userHeatAo;
        }

        List<HeatDaysAo> heatDaysAos = new ArrayList<>();
        for (Object userHeatRecordAo : list) {
            if (userHeatRecordAo instanceof UserHeatRecordAo){
                UserHeatRecordAo recordAo = (UserHeatRecordAo) userHeatRecordAo;
                HeatDaysAo heatDaysAo = new HeatDaysAo();
                heatDaysAo.setDays(getDays(recordAo.getTimestamp()));
                heatDaysAo.setScore(recordAo.getHeatScore());
                heatDaysAos.add(heatDaysAo);
            }
        }
        double heatScore = ruleTempFeature.executeHeat(heatDaysAos);
        userHeatAo.setHeatScore(heatScore);
        return userHeatAo;
    }

    @Override
    public List<UserHeatAo> getUsersHeat() {
        String pattern  = UserActionRedisKey.USER_HEAT_REDIS_KEY + "*";
        Collection<String> keys = redissonService.getKeysByPattern(pattern);

        // 1.计算时间戳
        long currentTime = System.currentTimeMillis();
        // 30天前的时间戳
        long thirtyDaysAgoTime = currentTime - FeatureConstant.FEATURE_EXPIRE_TIME_SECOND * 1000L;

        List<UserHeatAo> userHeatAos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                // 从ZSet获取数据
                Collection<Object> userHeatRecordAos = redissonService.zRangeByScore(
                        key,
                        (double) thirtyDaysAgoTime,
                        (double) currentTime);
                UserHeatAo userHeatAo = getUserHeatAo(userHeatRecordAos);
                // 提取 userId
                String[] parts = key.split(":");
                // 获取最后一个部分作为 userId
                String userIdStr = parts[parts.length - 1];
                try {
                    Long userId = Long.parseLong(userIdStr);
                    userHeatAo.setUserId(userId);
                    userHeatAos.add(userHeatAo);
                } catch (NumberFormatException e){
                    log.error("获取用户列表的活跃度异常，userId获取失败，userIdStr = {}", userIdStr);
                    continue;
                }
            }
        }
        return userHeatAos;
    }

    private int getDays(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long days = (currentTime - timestamp) / (1000L * 60 * 60 * 24);
        return (int) days;
    }
}
