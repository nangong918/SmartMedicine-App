package com.api.mapper.purchase.redis.impl;

import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.constant.purchase.PurchaseRedisKey;
import com.czy.api.domain.ao.purchase.OrderStatusAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *@author 13225
 *@date 2025/8/26 17:22
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PayRedisMapperImpl implements PayRedisMapper {

    private final RedissonClient redissonClient;

    @Override
    public void saveOrderStatus(@NotNull Long userId, @NotNull Long orderId,
                                @NotNull Integer customerStatus, @Nullable Integer merchantStatus) {
        String key = PurchaseRedisKey.Pay.ORDER_STATUS_EXPIRED + orderId + ":" + userId;

        OrderStatusAo ao = new OrderStatusAo();
        ao.setUserId(userId);
        ao.setOrderId(orderId);
        ao.setCustomerStatus(customerStatus);
        ao.setMerchantStatus(merchantStatus);
        redissonClient.getBucket(key).set(ao);

        RBucket<OrderStatusAo> bucket = redissonClient.getBucket(key);
        bucket.expire(PurchaseRedisKey.Pay.ORDER_STATUS_KEY_TIMEOUT, TimeUnit.MINUTES);
    }

    @Override
    public void deleteOrderStatus(@NotNull Long userId, @NotNull Long orderId) {
        String key = PurchaseRedisKey.Pay.ORDER_STATUS_EXPIRED + orderId + ":" + userId;
        redissonClient.getBucket(key).delete();
    }

    @Override
    public void updateOrderStatus(@NotNull Long userId, @NotNull Long orderId,
                                  @NotNull Integer customerStatus, @Nullable Integer merchantStatus){
        deleteOrderStatus(userId, orderId);
        saveOrderStatus(userId, orderId, customerStatus, merchantStatus);
    }

    @Override
    public OrderStatusAo getOrderStatus(@NotNull Long userId, @NotNull Long orderId) {
        String key = PurchaseRedisKey.Pay.ORDER_STATUS_EXPIRED + orderId + ":" + userId;
        RBucket<OrderStatusAo> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return bucket.get();
        }
        return null;
    }
}
