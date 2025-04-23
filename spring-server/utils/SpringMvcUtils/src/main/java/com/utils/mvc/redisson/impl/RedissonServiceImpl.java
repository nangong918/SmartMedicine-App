package com.utils.mvc.redisson.impl;

import com.alibaba.fastjson.JSON;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 13225
 * @date 2025/3/29 16:09
 */
@RequiredArgsConstructor
@Service
public class RedissonServiceImpl implements RedissonService {

    private static final Long EXPIRE_SECONDS = 3600L; // 如果为设置时间，默认为1小时
    private final RedissonClient redissonClient;

    @Override
    public boolean tryLock(RedissonClusterLock redisLock) {
        RLock lock = redissonClient.getLock(redisLock.getId());
        try {
            // 尝试获取锁，最多等待5秒，锁定时间为lockTimeout秒
            return lock.tryLock(5, redisLock.getLockTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(RedissonClusterLock redisLock) {
        RLock lock = redissonClient.getLock(redisLock.getId());
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
    public boolean expireKey(String key, long timeout) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.expire(timeout, TimeUnit.SECONDS);
    }

    @Override
    public long getExpireKey(String key) throws Exception {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        long remainingTime = bucket.remainTimeToLive();
        return remainingTime / 1000; // 转换为秒
    }

    @Override
    public boolean hasKey(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.isExists();
    }

    @Override
    public boolean persist(String key) throws Exception {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            bucket.clearExpire(); // 移除过期时间
            return true;
        } else {
            throw new Exception("Key does not exist.");
        }
    }

    @Override
    public boolean setObjectByJson(String key, Object obj, Long expireTimes) {
        try {
            String jsonString = JSON.toJSONString(obj); // 使用 Fastjson 序列化为 JSON
            RBucket<String> bucket = redissonClient.getBucket(key);
            long expireTime = expireTimes != null ? expireTimes : EXPIRE_SECONDS;
            bucket.set(jsonString, expireTime, TimeUnit.SECONDS); // 设置过期时间
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setObjectBySerializable(String key, Object obj, Long expireTimes) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        long expireTime = expireTimes != null ? expireTimes : EXPIRE_SECONDS;
        bucket.set(obj, expireTime, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public <T> T getObjectFromJson(String key, Class<T> clazz) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            String jsonString = bucket.get();
            if (jsonString != null) {
                return JSON.parseObject(jsonString, clazz);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public <T> T getObjectFromSerializable(String key, Class<T> clazz) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    @Override
    public boolean deleteObject(String key) {
        // 获取RBucket对象
        RBucket<Object> bucket = redissonClient.getBucket(key);

        // 删除键并返回结果
        return bucket.delete();
    }
}
