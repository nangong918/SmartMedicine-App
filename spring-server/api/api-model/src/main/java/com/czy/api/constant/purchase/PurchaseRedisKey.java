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
    }
}
