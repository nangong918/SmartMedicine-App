package com.czy.api.constant.feature;

/**
 * @author 13225
 * @date 2025/5/9 10:58
 */
public class FeatureConstant {


    public static final String serviceName = "feature-service";
    public static final String serviceRoute = "/" + serviceName;

    public static final String BurialPoint_CONTROLLER = "/burialPoint";
    public static final String serviceUri = "lb://" + serviceName;

    // 用户的临时特征只保存30天
    public static final Long FEATURE_EXPIRE_TIME_SECOND = 60 * 60 * 24 * 30L;
    public static final Long FEATURE_EXPIRE_TIME_DAY = 30L;
}
