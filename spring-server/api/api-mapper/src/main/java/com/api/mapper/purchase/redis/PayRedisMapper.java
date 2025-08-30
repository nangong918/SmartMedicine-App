package com.api.mapper.purchase.redis;

/**
 *@author 13225
 *@date 2025/8/26 17:22
 */
public interface PayRedisMapper {

    // 记录已经支付, 不再是待支付状态
    void saveOrderWaitPayStatus(Long orderId);

    // 获取是否已经支付, 如果已经支付还要删除redis key
    boolean getAndDeleteOrderWaitPayStatus(Long orderId);

    void saveOrderOutTimeStatus(Long orderId, Long userId, boolean isOutTime);

    boolean getAndDeleteOrderOutTimeStatus(Long orderId, Long userId);
}
