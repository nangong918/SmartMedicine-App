package com.czy.api.constant.feature;

/**
 * @author 13225
 * @date 2025/5/9 18:00
 */
public interface UserActionRedisKey {

    String USER_FEATURE_REDIS_KEY = "user_feature:";
    String POST_HEAT_REDIS_KEY = "post_heat:";

    // feature
    String USER_FEATURE_ENTITY_LABEL_REDIS_KEY = USER_FEATURE_REDIS_KEY + "entity_label:";

    // heat

    // city
    String USER_FEATURE_CITY_LOCATION_REDIS_KEY = USER_FEATURE_REDIS_KEY + "city:";

    // click
    String USER_FEATURE_CLICK_POST_REDIS_KEY = USER_FEATURE_REDIS_KEY + "click_post:";
    String POST_HEAT_CLICK_REDIS_KEY = POST_HEAT_REDIS_KEY + "clicked:";

    // browse
    String USER_FEATURE_BROWSE_POST_REDIS_KEY = USER_FEATURE_REDIS_KEY + "browse_post:";
    String POST_HEAT_BROWSE_REDIS_KEY = POST_HEAT_REDIS_KEY + "browsed:";

    // search
    String USER_FEATURE_SEARCH_POST_REDIS_KEY = USER_FEATURE_REDIS_KEY + "search_post:";
    // operation
    String USER_FEATURE_OPERATION_POST_REDIS_KEY = USER_FEATURE_REDIS_KEY + "operation_post:";
    // comment
    String USER_FEATURE_COMMENT_POST_REDIS_KEY = USER_FEATURE_REDIS_KEY + "comment_post:";

    // postHeatList
    String POST_HEAT_LIST_REDIS_KEY_PREFIX = POST_HEAT_REDIS_KEY + "list";
}
