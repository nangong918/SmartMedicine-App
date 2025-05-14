package com.utils.mvc.redisson.impl;

import com.alibaba.fastjson.JSON;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/3/29 16:09
 */
@Slf4j
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

    @Override
    public void saveHashMap(String key, HashMap<String, String> data, Long expireTimes) {
        RMap<String, String> map = redissonClient.getMap(key);
        map.putAll(data);
        if (expireTimes != null){
            map.expire(expireTimes, TimeUnit.SECONDS);
        }
        else {
            map.expire(EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Override
    public void saveObjectHaseMap(String key, HashMap<String, Object> data, Long expireTimes) {
        RMap<String, Object> map = redissonClient.getMap(key);
        map.putAll(data);
        if (expireTimes != null){
            map.expire(expireTimes, TimeUnit.SECONDS);
        }
        else {
            map.expire(EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }

    @Override
    public HashMap<String, String> getHashMap(String key) {
        RMap<String, String> map = redissonClient.getMap(key);
        return new HashMap<>(map.readAllMap());
    }

    @Override
    public HashMap<String, Object> getObjectHaseMap(String key) {
        RMap<String, Object> map = redissonClient.getMap(key);
        return new HashMap<>(map.readAllMap());
    }

    @Override
    public void updateHashMap(String hashKey, String field, String value) {
        RMap<String, String> map = redissonClient.getMap(hashKey);
        map.put(field, value);
    }

    @Override
    public void deleteHashMap(String redisKey) {
        RMap<String, String> map = redissonClient.getMap(redisKey);
        map.delete();
    }

    @Override
    public void deleteFieldFromHash(String redisKey, String hashKey) {
        RMap<String, String> map = redissonClient.getMap(redisKey);
        map.remove(hashKey);
    }


    private void setExpire(RScoredSortedSet<Object> zSet, Long expireTime) {
        if (expireTime == null) {
            zSet.expire(EXPIRE_SECONDS, TimeUnit.SECONDS);
        } else {
            zSet.expire(expireTime, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean zAdd(String key, Object value, double score, Long expireTime) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        boolean result = zSet.add(score, value);
        setExpire(zSet, expireTime);
        return result;
    }

    @Override
    public int zAddAll(String key, Map<Object, Double> values, Long expireTime) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        int count = 0;
        for (Map.Entry<Object, Double> entry : values.entrySet()) {
            if (zSet.add(entry.getValue(), entry.getKey())) {
                count++;
            }
        }
        setExpire(zSet, expireTime);
        return count;
    }

    @Override
    public boolean zRemove(String key, Long expireTime, Object... values) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        return zSet.removeAll(Arrays.asList(values));
    }

    @Override
    public Double zGetScore(String key, Object value) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        return zSet.getScore(value);
    }

    @Override
    public boolean zUpdateScore(String key, Object value, double newScore, Long expireTime) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        boolean result = zSet.add(newScore, value);
        setExpire(zSet, expireTime);
        return result;
    }

    @Override
    public int zSize(String key) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        return zSet.size();
    }

    @Override
    public int zRank(String key, Object value) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        Integer rank = zSet.rank(value);
        return rank == null ? -1 : rank;
    }

    @Override
    public int zReverseRank(String key, Object value) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        Integer rank = zSet.revRank(value);
        return rank == null ? -1 : rank;
    }

    @Override
    public Collection<Object> zRange(String key, int start, int end) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        return zSet.valueRange(start, end);
    }

    @Override
    public Collection<Object> zReverseRange(String key, int start, int end) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        return zSet.valueRangeReversed(start, end);
    }

    @Override
    public Collection<Object> zRangeByScore(String key, double minScore, double maxScore) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        return zSet.valueRange(minScore, true, maxScore, true);
    }

    @Override
    public Map<Object, Double> zRangeByScoreWithScores(String key, double minScore, double maxScore) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        return zSet.entryRange(minScore, true, maxScore, true).stream()
                .collect(Collectors.toMap(ScoredEntry::getValue, ScoredEntry::getScore));
    }

    @Override
    public int zRemoveRangeByRank(String key, int start, int end, Long expireTime) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        int count = zSet.removeRangeByRank(start, end);
        if (count > 0) {
            setExpire(zSet, expireTime);
        }
        return count;
    }

    @Override
    public int zRemoveRangeByScore(String key, double minScore, double maxScore, Long expireTime) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        int count = zSet.removeRangeByScore(minScore, true, maxScore, true);
        if (count > 0) {
            setExpire(zSet, expireTime);
        }
        return count;
    }

    @Override
    public boolean zClear(String key, Long expireTime) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        boolean result = zSet.delete();
        if (result && expireTime != null) {
            // 重新创建空集合并设置过期时间
            RScoredSortedSet<Object> newZSet = redissonClient.getScoredSortedSet(key);
            setExpire(newZSet, expireTime);
        }
        return result;
    }
}
