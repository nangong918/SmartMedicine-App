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
        String WAIT_PAY_START_TIME_KEY_PREFIX = ID + "waitPayStartTime:";
        // 待付款的key超时时间，注意是key，不是订单本身。这个key就是记录订单审核通过的时间，用于判断是否过期，如果没有这个key订单就是错误状态，所以key的ttl要大于订单的ttl
        // 订单的ttl是5~15min，key的ttl是30min
        long WAIT_PAY_KEY_TIMEOUT = 30 * 60L;
    }
}
