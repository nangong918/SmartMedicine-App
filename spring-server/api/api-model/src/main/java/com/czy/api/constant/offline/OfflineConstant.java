package com.czy.api.constant.offline;

/**
 * @author 13225
 * @date 2025/5/16 11:19
 */
public class OfflineConstant {

    public static final String serviceName = "recommend-offline-service";
    public static final String serviceRoute = "/" + serviceName;
    public static final String serviceUri = "lb://" + serviceName;

    public static final int TOP_ENTITY_NUM = 3;
}
