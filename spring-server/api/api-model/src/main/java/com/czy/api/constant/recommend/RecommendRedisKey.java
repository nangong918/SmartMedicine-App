package com.czy.api.constant.recommend;

/**
 * @author 13225
 * @date 2025/5/20 17:47
 */
public interface RecommendRedisKey {
    String NEAR_ONLINE_RESULT_KEY  = "near_online_result:";
    Long NEAR_ONLINE_RESULT_EXPIRE_TIME = (long) 60 * 60 * 24;
}
