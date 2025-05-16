package com.czy.api.constant.offline;

/**
 * @author 13225
 * @date 2025/5/16 11:21
 */
public interface OfflineRedisConstant {
    // offline用户热度ZSet的key
    String OFFLINE_USER_HEAT_KEY = "offline_user_heat";
    // offline用户热搜是否已经计算了
    String OFFLINE_USER_HEAT_CALCULATE_FLAG = "offline_user_heat_calculate_flag";
    // offline用户热度是否已经计算的flag：提前一分钟删除
    Long offlineUserHeatCalculateFlagExpireTime = 60 * 60 * 24L - 60L;
    // 一天
    Long ONE_DAY = 60 * 60 * 24L;

    // userHistoryFeature的Key
    String USER_HISTORY_FEATURE_KEY = "user_history_feature";
    // userHistoryFeature的过期时间：5天；因为单天的话是近线层的临时特征
    Long USER_HISTORY_FEATURE_EXPIRE_TIME = 60 * 60 * 24L * 5;
}
