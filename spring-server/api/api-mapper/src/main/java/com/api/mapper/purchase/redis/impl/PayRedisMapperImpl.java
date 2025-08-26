package com.api.mapper.purchase.redis.impl;

import com.api.mapper.purchase.redis.PayRedisMapper;
import com.czy.api.constant.purchase.PurchaseRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 *@author 13225
 *@date 2025/8/26 17:22
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PayRedisMapperImpl implements PayRedisMapper {

    private final RedissonClient redissonClient;

    // 记录订单待支付状态
    @Override
    public void saveOrderWaitPayStatus(Long orderId){
        String key = PurchaseRedisKey.Pay.IS_WAIT_PAY_KEY_PREFIX + orderId;
        RBucket<Boolean> bucket = redissonClient.getBucket(key);
        bucket.set(true);
    }

    // 获取订单是否还是待支付状态
    @Override
    public boolean getAndDeleteOrderWaitPayStatus(Long orderId){
        String key = PurchaseRedisKey.Pay.IS_WAIT_PAY_KEY_PREFIX + orderId;
        RBucket<Boolean> bucket = redissonClient.getBucket(key);
        boolean exists = bucket.isExists() && bucket.get();
        if (bucket.isExists()) {
            bucket.delete();
        }
        return exists;
    }
}
