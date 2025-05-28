package com.czy.api.constant.recommend;

/**
 * @author 13225
 * @date 2025/5/20 17:47
 */
public interface RecommendRedisKey {
    String NEAR_ONLINE_RESULT_KEY  = "near_online_result:";
    Long NEAR_ONLINE_RESULT_EXPIRE_TIME = (long) 60 * 60 * 24;


    // 分布式锁
    // 单次点击推荐锁定超时时间：5s（防止用户多次点击，这只是锁过期时间，而不是必须5秒点击一下）
    long clickRecommendLockTimeout = 5L;
    // 多次点击之后对用户进行冷静锁；次数：5次
    long clickRecommendTimesMax = 5L;
    // 记录用户点击频繁保留时间：30s
    long clickRecommendTimesSaveTimeout = 30L;
    // 用户频繁点击次数的redisKey
    String clickRecommendTimesKey = "click_recommend_times:";
    // 多次点击之后对用户进行冷静的时间：10s（锁定时间和失效时间）
    long clickRecommendSleepTimeout = 10L;
}
