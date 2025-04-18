package com.utils.webflux.redis.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.webflux.redis.ReactiveRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/4 20:32
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class ReactiveRedisServiceImpl implements ReactiveRedisService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // ---------------------Key操作---------------------

    /**
     * 设置键的过期时间
     */
    public Mono<Boolean> expireKey(String key, long timeout) {
        return redisTemplate.expire(key, Duration.ofSeconds(timeout))
                .onErrorResume(e -> {
                    log.error("设置键过期时间失败, key: {}", key, e);
                    return Mono.just(false);
                });
    }


    /**
     * 获取指定键的剩余过期时间
     */
    public Mono<Long> getExpireKey(String key) {
        return redisTemplate.getExpire(key)
                .map(Duration::getSeconds)
                .switchIfEmpty(Mono.error(new RuntimeException("键不存在或没有设置过期时间")))
                .onErrorResume(e -> {
                    log.error("获取键过期时间失败, key: {}", key, e);
                    return Mono.error(e);
                });
    }

    /**
     * 检查指定键是否存在
     */
    public Mono<Boolean> hasKey(String key) {
        return redisTemplate.hasKey(key)
                .onErrorResume(e -> {
                    log.error("检查键是否存在失败, key: {}", key, e);
                    return Mono.just(false);
                });
    }

    /**
     * 移除指定键的过期时间
     */
    public Mono<Boolean> persist(String key) {
        return redisTemplate.persist(key)
                .switchIfEmpty(Mono.error(new RuntimeException("键不存在")))
                .onErrorResume(e -> {
                    log.error("移除键过期时间失败, key: {}", key, e);
                    return Mono.error(e);
                });
    }

    /**
     * 将对象序列化为JSON字符串存储
     */
    public Mono<Boolean> setObjectAsString(String key, Object obj, Long expireTimes) {
        try {
            String value = objectMapper.writeValueAsString(obj);
            Mono<Boolean> result = redisTemplate.opsForValue().set(key, value);
            if (expireTimes != null) {
                result = result.then(expireKey(key, expireTimes));
            }
            return result.onErrorResume(e -> {
                log.error("设置对象为字符串失败, key: {}", key, e);
                return Mono.just(false);
            });
        } catch (JsonProcessingException e) {
            log.error("对象序列化失败", e);
            return Mono.just(false);
        }
    }

    /**
     * 从字符串反序列化对象
     */
    public <T> Mono<T> getObjectFromString(String key, Class<T> clazz) {
        return redisTemplate.opsForValue().get(key)
                .flatMap(value -> {
                    try {
                        return Mono.just(objectMapper.readValue(value.toString(), clazz));
                    } catch (JsonProcessingException e) {
                        log.error("字符串反序列化失败", e);
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> {
                    log.error("获取字符串对象失败, key: {}", key, e);
                    return Mono.empty();
                });
    }

    // ---------------------删除操作---------------------

    /**
     * 删除缓存
     */
    public Mono<Boolean> deleteAny(String key) {
        return redisTemplate.delete(key)
                .map(count -> count > 0)
                .onErrorResume(e -> {
                    log.error("删除缓存失败, key: {}", key, e);
                    return Mono.just(false);
                });
    }

    /**
     * 删除hash中的字段
     */
    public Mono<Long> hashDelete(String key, String hashKey) {
        return redisTemplate.opsForHash().remove(key, hashKey)
                .onErrorResume(e -> {
                    log.error("删除hash字段失败, key: {}, hashKey: {}", key, hashKey, e);
                    return Mono.just(0L);
                });
    }

    /**
     * 按前缀删除缓存
     */
    public Mono<Boolean> deleteByPrefix(String prefix) {
        return redisTemplate.keys(prefix + "*")
                .flatMap(redisTemplate::delete)
                .then(Mono.just(true))
                .onErrorResume(e -> {
                    log.error("按前缀删除缓存失败, prefix: {}", prefix, e);
                    return Mono.just(false);
                });
    }

    /**
     * 获取指定前缀的所有键
     */
    public Mono<Set<String>> getKeyByPrefix(String prefix) {
        return redisTemplate.keys(prefix + "*")
                .collect(Collectors.toSet())
                .onErrorResume(e -> {
                    log.error("获取前缀键失败, prefix: {}", prefix, e);
                    return Mono.empty();
                });
    }


}
