package com.czy.api.constant.post;

/**
 * @author 13225
 * @date 2025/4/18 18:20
 */
public class PostConstant {
    // 缓存
    public static final String POST_PUBLISH_KEY = "post_publish_key:";
    public static final String POST_UPDATE_KEY = "post_update_key:";
    // 默认oss上传最大时间是5分钟，超过5分钟就删掉了
    public static final Long POST_CHANGE_KEY_EXPIRE_TIME = 60 * 5L;

    public static final String serviceName = "post-service";
    // serviceRoute
    public static final String serviceRoute = "/" + serviceName;
    // Post_CONTROLLER
    public static final String Post_CONTROLLER = "/post";
    // postPublishFirst
    public static final String POST_PUBLISH_FIRST = "/postPublishFirst";
    // postUpdateAll
    public static final String POST_UPDATE_ALL = "/postUpdateAll";
    // serviceUri
    public static final String serviceUri = "lb://" + serviceName;
}
