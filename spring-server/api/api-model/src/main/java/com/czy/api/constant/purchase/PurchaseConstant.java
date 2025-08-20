package com.czy.api.constant.purchase;

/**
 * @author 13225
 * @date 2025/4/18 18:20
 */
public class PurchaseConstant {
    public static final String serviceName = "purchase-service";
    // serviceRoute
    public static final String serviceRoute = "/" + serviceName;

    // serviceUri
    public static final String serviceUri = "lb://" + serviceName;

    // 支付超时时间、某商品重复点击订单的分布式锁超时时间 (秒)
    public static final long PAY_TIMEOUT = 5 * 60L;
}
