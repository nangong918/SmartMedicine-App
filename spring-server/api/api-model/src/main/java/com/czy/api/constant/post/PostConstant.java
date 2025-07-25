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
    public static final Long POST_CHANGE_KEY_EXPIRE_TIME = 15L;

    public static final String serviceName = "post-service";
    // serviceRoute
    public static final String serviceRoute = "/" + serviceName;
    // Post_CONTROLLER
    public static final String Post_CONTROLLER = "/post";
    // UserBriefController
    public static final String USER_BRIEF_CONTROLLER = "/userBrief";
    // postPublishFirst
    public static final String POST_PUBLISH_FIRST = "/postPublishFirst";
    // postUpdateAll
    public static final String POST_UPDATE_ALL = "/postUpdateAll";
    // serviceUri
    public static final String serviceUri = "lb://" + serviceName;
    public static final String POST_FILE_CONTROLLER = "/postFile";
    // 默认文件夹
    public static final String DEFAULT_COLLECT_FOLDER_NAME = "默认收藏夹";
    // 一次获取的comment数量
    public static final Integer COMMENT_PAGE_SIZE = 20;
}
