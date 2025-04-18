package com.czy.springUtils.manager;

import lombok.Data;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tl
 * @date 17/2/16
 */
@Data
public abstract class AbstractRedisTemplate<T> {

    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     * @param key    键
     * @param value  值
     */
    public void set(String key, T value) {
        // 管理和区分不同类型的键
        key = getKeyType() + key;
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key.intern(), value, getExpireSecond(), TimeUnit.SECONDS);
    }

    public boolean setIfAbsent(String key, byte[] value) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return Boolean.TRUE.equals(valueOperations.setIfAbsent(key, value));
    }

    @SuppressWarnings("unchecked")
    public T get(String key) throws Exception {
        key = getKeyType() + key;
        try {
            Object operation = redisTemplate.opsForValue().get(key.intern());
            return (T) operation;
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    abstract protected String getKeyType();

    abstract protected long getExpireSecond();

    public void setList(String key, List<?> value) {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(key, value);
    }

    public Object getList(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public void setSet(String key, Set<?> value) {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        setOperations.add(key, value);
    }

    public Object getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public void setHash(long key, Object value) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(getKeyType(), key, value);
    }

    @SuppressWarnings("unchecked")
    public T getHash(long hashKey) {
        Object value = redisTemplate.opsForHash().get(getKeyType(), hashKey);
        return (T) value; // 可能需要 @SuppressWarnings("unchecked")
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean expire(String key) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, getExpireSecond(), TimeUnit.SECONDS));
    }
}
