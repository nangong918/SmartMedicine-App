package com.utils.webflux.redis;

import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author 13225
 * @date 2025/4/4 20:31
 */
public interface ReactiveRedisService {

    /**
     * 设置键的过期时间
     */
    Mono<Boolean> expireKey(String key, long timeout);
    /**
     * 获取指定键的剩余过期时间
     */
    Mono<Long> getExpireKey(String key);
    /**
     * 检查指定键是否存在
     */
    Mono<Boolean> hasKey(String key);
    /**
     * 移除指定键的过期时间
     */
    Mono<Boolean> persist(String key);
    /**
     * 将对象序列化为JSON字符串存储
     */
    Mono<Boolean> setObjectAsString(String key, Object obj, Long expireTimes);
    /**
     * 从字符串反序列化对象
     */
    <T> Mono<T> getObjectFromString(String key, Class<T> clazz);
    /**
     * 删除缓存
     */
    Mono<Boolean> deleteAny(String key);
    /**
     * 删除hash中的字段
     */
    Mono<Long> hashDelete(String key, String hashKey);
    /**
     * 按前缀删除缓存
     */
    Mono<Boolean> deleteByPrefix(String prefix);
    /**
     * 获取指定前缀的所有键
     */
    Mono<Set<String>> getKeyByPrefix(String prefix);
}
