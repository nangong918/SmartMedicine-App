package com.czy.api.constant.recommend;

/**
 * @author 13225
 * @date 2025/5/20 17:46
 */
public class RecommendConstant {

    public static final String serviceName = "recommend-service";
    // serviceRoute
    public static final String serviceRoute = "/" + serviceName;
    public static final String RECOMMEND_CONTROLLER = "/recommend";
    public static final String RECOMMEND_POSTS = "/getPost";
    // serviceUri
    public static final String serviceUri = "lb://" + serviceName;
}
