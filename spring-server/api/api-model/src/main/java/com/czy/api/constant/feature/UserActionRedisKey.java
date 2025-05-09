package com.czy.api.constant.feature;

/**
 * @author 13225
 * @date 2025/5/9 18:00
 */
public interface UserActionRedisKey {

    String USER_FEATURE_REDIS_KEY = "user_feature:";
    String POST_HEAT_REDIS_KEY = "post_heat:";

    String USER_FEATURE_CITY_LOCATION_REDIS_KEY = USER_FEATURE_REDIS_KEY + "city:";

    String USER_FEATURE_CLICK_POST_REDIS_KEY = USER_FEATURE_REDIS_KEY + "click_post:";
    String POST_HEAT_CLICK_REDIS_KEY = POST_HEAT_REDIS_KEY + "clicked:";
}
