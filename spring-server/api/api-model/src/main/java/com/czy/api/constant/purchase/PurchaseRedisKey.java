package com.czy.api.constant.purchase;

/**
 * @author 13225
 * @date 2025/8/26 16:59
 */
public interface PurchaseRedisKey {
    String ID = "purchase:";
    interface Pay {
        String ID = "pay:";
        // key = keyPrefix + orderId
        String IS_WAIT_PAY_KEY_PREFIX = ID + "waitPay:";
        long IS_PAY_EXPIRED_TIMEOUT = 24 * 60 * 60L;
        // 是否过期
        String IS_PAY_EXPIRED = ID + "isExpired:";
        String ORDER_STATUS_EXPIRED = ID + "orderStatus:";
        // 订单状态4天过期
        long ORDER_STATUS_KEY_TIMEOUT = 4 * 24 * 60 * 60L;
    }
}
