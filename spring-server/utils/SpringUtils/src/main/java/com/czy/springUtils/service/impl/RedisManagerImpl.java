package com.czy.springUtils.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.czy.springUtils.redis.RedisClusterLock;
import com.czy.springUtils.redis.SysCache;
import com.czy.springUtils.service.RedisManagerService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2024/12/30 17:20
 * Set:快找慢存     [找多]
 * Hash:快存慢找    [存多]
 * ZSet:排序，需要根据最近活跃度或优先级来推送消息时
 */
@Slf4j
@Service
public class RedisManagerImpl implements RedisManagerService {

    private static final Long EXPIRE_SECONDS = 3600L; // 如果为设置时间，默认为1小时
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public RedisClusterLock createLock(String type, String key) {
        return new RedisClusterLock(type + "_" + key, redisTemplate);
    }

    @Override
    public RedisClusterLock createLock(String type, String key, long expireTime) {
        return null;
    }

    @Override
    public boolean getLock(String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, 1, Duration.ofSeconds(30)));
    }

    @Override
    public boolean getLock(String key, Long expireTime) {
        if (expireTime == null) {
            expireTime = EXPIRE_SECONDS;
        }
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, 1, Duration.ofSeconds(expireTime)));
    }

    @Override
    public boolean expireKey(String key, long timeout) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS)); // 设置过期时间
    }

    @Override
    public long getExpireKey(String key) throws Exception {
        Long timeout = redisTemplate.getExpire(key, TimeUnit.SECONDS); // 获取剩余过期时间
        if (timeout == null || timeout < 0) {
            throw new Exception("Key does not exist or has no expiration");
        }
        return timeout; // 返回剩余过期时间
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key)); // 检查指定键是否存在
    }

    @Override
    public boolean persist(String key) throws Exception {
        Boolean result = redisTemplate.persist(key); // 移除过期时间
        if (result == null) {
            throw new Exception("Key does not exist");
        }
        else return result;// 返回布尔值，表示是否成功移除过期时间
    }

    @Override
    public boolean setObjectAsString(String key, Object obj, Long expireTimes) {
        try {
            redisTemplate.opsForValue().set(key, obj); // 存储对象
            if (expireTimes != null) {
                redisTemplate.expire(key, expireTimes, TimeUnit.SECONDS); // 设置过期时间
            }
            return true;
        } catch (Exception e) {
            log.error("Error setting object as string in Redis", e);
            return false; // 处理异常，返回失败
        }
    }

    @Override
    public boolean setObjectAsString(Object object, Object obj, Long expireTimes) {
        String jsonString = JSON.toJSONString(obj);
        return setObjectAsString(jsonString, obj, expireTimes);
    }

    @Override
    public boolean setObjectAsHash(String key, Object obj, Long expireTimes) {
        try {
            Map<?, ?> map = convertObjectToMap(obj); // 将对象转换为 Map
            redisTemplate.opsForHash().putAll(key, map); // 存储对象为哈希
            if (expireTimes != null) {
                redisTemplate.expire(key, expireTimes, TimeUnit.SECONDS); // 设置过期时间
            }
            return true;
        } catch (Exception e) {
            log.error("Error setting object as hash in Redis", e);
            return false; // 处理异常，返回失败
        }
    }

    @Override
    public <T> T getObjectFromString(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key); // 获取值
            return convertValueToObject(value, clazz); // 转换为目标对象
        } catch (Exception e) {
            log.error("Error getting object from string in Redis", e);
            return null; // 处理反序列化失败
        }
    }

    @Override
    public <T> T getObjectFromHash(String key, Class<T> clazz) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key); // 获取哈希值
            return convertMapToObject(entries, clazz); // 转换为目标对象
        } catch (Exception e) {
            log.error("Error getting object from hash in Redis", e);
            return null; // 处理反序列化失败
        }
    }

    private Map<?, ?> convertObjectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        // 将对象转换为 JSON 字符串，然后再转换为 Map
        // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
        String jsonString = JSON.toJSONString(obj);
        return JSON.parseObject(jsonString, HashMap.class);
    }

    private <T> T convertValueToObject(Object value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        // 将 JSON 字符串转换为目标对象
        // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
        String jsonString = JSON.toJSONString(value);
        return JSON.parseObject(jsonString, clazz);
    }

    private <T> T convertMapToObject(Map<Object, Object> map, Class<T> clazz) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        // 将 Map 转换为 JSON 字符串，然后转换为目标对象
        // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
        String jsonString = JSON.toJSONString(map);
        return JSON.parseObject(jsonString, clazz);
    }

    @Override
    public boolean setObjectListAsString(String key, List<?> list, Long expireTimes) {
        if (list == null || list.isEmpty()){
            log.warn("List is empty or null, no data to store.");
            return false;
        }
        try {
            // 将列表中的每个对象转为 JSON 字符串并存储到 Redis 列表中
            for (Object item : list) {
                // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
                String jsonString = JSON.toJSONString(item);
                redisTemplate.opsForList().rightPush(key, jsonString); // 存储为列表
            }

            // 设置过期时间
            if (expireTimes != null) {
                redisTemplate.expire(key, expireTimes, TimeUnit.SECONDS); // 设置过期时间
            }
            return true;
        } catch (Exception e) {
            log.error("Error setting object list as string in Redis", e);
            return false;
        }
    }

    @Override
    public <T> List<T> getObjectListFromString(String key, Class<T> clazz) {
        try {
            List<Object> elements = redisTemplate.opsForList().range(key, 0, -1);
            if (elements != null) {
                return elements.stream()
                        .map(element -> JSON.parseObject((String) element, clazz)) // 解析为目标类型
                        .collect(Collectors.toList());
            }
            return Collections.emptyList(); // 返回空列表而不是 null
        } catch (Exception e) {
            log.error("Error getting object list from string in Redis", e);
            return null;
        }
    }

    @Override
    public <T> List<T> range(String key, long start, long end, Class<T> clazz) {
        try {
            List<Object> elements = redisTemplate.opsForList().range(key, start, end); // 获取列表范围
            if (elements != null && !elements.isEmpty()){
                return elements.stream()
                        .map(element -> {
                            // String 不能强转 (T) 而是需要用JSON解析
                            String jsonString = (String) element;
                            return JSON.parseObject(jsonString, clazz); // 使用 JSON 库解析
                        }) // 转换为目标类型
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error getting object list from hash in Redis", e);
            return null;
        }
    }

    @Override
    public boolean addElementToList(String key, Object element) {
        if (key == null || element == null) {
            log.warn("Key or element is null, no data to store.");
            return false; // key或element为空
        }
        try {
            // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
            redisTemplate.opsForList().rightPush(key, JSON.toJSONString(element));
            return true;
        } catch (Exception e) {
            // 记录异常日志
            log.error("Error adding element to list in Redis", e);
            return false;
        }
    }

    @Override
    public boolean removeElementFromList(String key, Object element) {
        if (key == null || element == null) {
            log.warn("Key or element is null, no data to store.");
            return false; // key或element为空
        }
        try {
            // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
            Long count = redisTemplate.opsForList().remove(key, 1, JSON.toJSONString(element));
            return count != null && count > 0;
        } catch (Exception e) {
            // 记录异常日志
            log.error("Error removing element from list in Redis", e);
            return false;
        }
    }

    @Override
    public boolean setObjectSetAsString(String key, Set<?> set, Long expireTimes) {
        if (set == null || set.isEmpty()){
            log.warn("Set is empty or null, no data to store.");
            return false;
        }
        try {
            // 将列表中的每个对象转为 JSON 字符串并存储到 Redis 列表中
            for (Object item : set) {
                // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
                String jsonString = JSON.toJSONString(item);
                redisTemplate.opsForSet().add(key, jsonString);
            }

            if (expireTimes != null) {
                redisTemplate.expire(key, expireTimes, TimeUnit.SECONDS); // 设置过期时间
            }
            return true;
        } catch (Exception e) {
            log.error("Error setting object list as string in Redis", e);
            return false;
        }
    }

    @Override
    public boolean setObjectSetAsZSet(String key, Set<?> set, Double score, Long expireTimes) {
        try {
            for (Object item : set) {
                // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
                redisTemplate.opsForZSet().add(key, JSON.toJSONString(item), score); // 存储为有序集合
            }
            if (expireTimes != null) {
                redisTemplate.expire(key, expireTimes, TimeUnit.SECONDS); // 设置过期时间
            }
            return true;
        } catch (Exception e) {
            log.error("Error setting object list as hash in Redis", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> getObjectSetFromString(String key, Class<T> clazz) {
        try {
            Set<Object> members = redisTemplate.opsForSet().members(key); // 获取集合中的所有元素
            if (members != null) {
                return members.stream()
                        .map(member -> {
                            // 解决是String时候的问题
                            if (clazz == String.class) {
                                // 直接返回字符串而不进行 JSON 解析
                                return (T) member; // 强制转换为 T
                            }
                            // 解决是Object时候的问题
                            else if (clazz == Object.class) {
                                // 对于 Object.class，直接返回原始对象
                                return (T) member; // 强制转换为 T
                            }
                            else {
                                // 解析为目标类型
                                return JSON.parseObject((String) member, clazz);
                            }
                        })
                        .collect(Collectors.toSet());
            }
            return Collections.emptySet(); // 返回空集合而不是 null
        } catch (Exception e) {
            log.error("Error getting object list from string in Redis", e);
            return null;
        }
    }

    @Override
    public <T> Set<T> getObjectSetFromZSet(String key, Class<T> clazz) {
        try {
            Set<Object> elements = redisTemplate.opsForZSet().range(key, 0, -1); // 获取整个有序集合
            if (elements != null && !elements.isEmpty()){
                return elements.stream()
                        .map(element -> JSON.parseObject((String) element, clazz)) // 反序列化为集合
                        .collect(Collectors.toSet());
            }
            return Collections.emptySet(); // 返回空集合而不是 null
        } catch (Exception e) {
            log.error("Error getting object list from hash in Redis", e);
            return null;
        }
    }

    @Override
    public void addSetEle(String key, long expireTime, String... values) {
        try {
            for (String value : values) {
                redisTemplate.opsForSet().add(key, value); // 添加元素到 Set
            }
            if (expireTime > 0) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS); // 设置过期时间
            }
        } catch (Exception e) {
            log.error("Error adding set element in Redis", e);
        }
    }

    @Override
    public void addSetEle(String key, String... values) {
        try {
            redisTemplate.opsForSet().add(key, values); // 添加元素到 Set
        } catch (Exception e) {
            log.error("Error adding set element in Redis", e);
        }
    }

    @Override
    public boolean addElementToSet(String key, Object element) {
        if (key == null || element == null) {
            log.warn("Error adding set element in Redis: key or element is null");
            return false; // key或element为空
        }
        try {
            // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
            redisTemplate.opsForSet().add(key, JSON.toJSONString(element));
            return true;
        } catch (Exception e) {
            // 记录异常日志
            log.error("Error adding set element in Redis", e);
            return false;
        }
    }

    @Override
    public boolean removeElementFromSet(String key, Object element) {
        if (key == null || element == null) {
            log.warn("Error removing set element in Redis: key or element is null");
            return false; // key或element为空
        }
        try {
            // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
            Long count = redisTemplate.opsForSet().remove(key, JSON.toJSONString(element));
            return count != null && count > 0;
        } catch (Exception e) {
            // 记录异常日志
            log.error("Error removing set element in Redis", e);
            return false;
        }
    }

    @Override
    public boolean addElementToZSet(String key, Object element, Double score) {
        // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
        redisTemplate.opsForZSet().add(key, JSON.toJSONString(element), score);
        return true;
    }

    @Override
    public boolean removeElementFromZSet(String key, Object element) {
        // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
        Long count = redisTemplate.opsForZSet().remove(key, JSON.toJSONString(element));
        return count != null && count > 0;
    }

    @Override
    public boolean setObjectMapAsHash(String key, Map<?, ?> map, Long expireTimes) {
        if (map == null || map.isEmpty()) {
            log.warn("Map is empty or null, no data to store.");
            return false; // 处理空的 map
        }

        try {
//            for (Map.Entry<?, ?> entry : map.entrySet()) {
//                redisTemplate.opsForHash().put(key, entry.getKey(), JSON.toJSONString(entry.getValue())); // 存储为哈希
//            }
            map.forEach((k, v) ->
                    // 可以不用JSON.toJSONString，由RedisConfig配置，直接JDK的Jackson2JsonRedisSerializer序列化
                    redisTemplate.opsForHash().put(key, k, JSON.toJSONString(v)) // 存储为哈希
            );
            if (expireTimes != null) {
                redisTemplate.expire(key, expireTimes, TimeUnit.SECONDS); // 设置过期时间
            }
            return true;
        } catch (Exception e) {
            log.error("Error setting object map as hash in Redis", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, K, V> Map<K, V> getObjectMapFromHash(String key, @NotNull Class<T> clazz) {
        try {
//            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key); // 获取哈希值
            Map<?, ?> entries = redisTemplate.opsForHash().entries(key); // 获取哈希值 Map<?, ?> 与 Map<Object, Object>等效
//            return entries.entrySet().stream()
//                    .collect(Collectors.toMap(
//                            entry -> (K) entry.getKey(),
//                            entry -> JSON.parseObject((String) entry.getValue(), valueType) // 使用指定类型
//                    ));
            Map<K, Object> entryMap = entries.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> (K) entry.getKey(),
                            entry -> JSON.parseObject((String) entry.getValue(), clazz))); // 反序列化为 Map
            return (Map<K, V>) entryMap;
        } catch (Exception e) {
            log.error("Error getting object map from hash in Redis", e);
            return null;
        }
    }

    @Override
    public boolean addElementToMap(String key, Object mapKey, Object mapValue) {
        if (key == null || mapKey == null || mapValue == null) {
            log.warn("Error putting element to map in Redis: key, mapKey, or mapValue is null");
            return false; // key、mapKey或mapValue为空
        }
        try {
            redisTemplate.opsForHash().put(key, mapKey, JSONObject.toJSONString(mapValue));
            return true;
        } catch (Exception e) {
            // 记录异常日志
            log.error("Error putting element to map in Redis", e);
            return false;
        }
    }

    @Override
    public boolean removeElementFromMap(String key, Object mapKey) {
        if (key == null || mapKey == null) {
            log.warn("Error removing element from map in Redis: key or mapKey is null");
            return false; // key或mapKey为空
        }
        try {
            Long count = redisTemplate.opsForHash().delete(key, mapKey);
            return count > 0;
        } catch (Exception e) {
            // 记录异常日志
            log.error("Error removing element from map in Redis", e);
            return false;
        }
    }

    @Override
    public boolean updateElementInMap(String key, Object mapKey, Object mapValue) {
        if (key == null || mapKey == null) {
            log.warn("Error update element from map in Redis: key or mapKey is null");
            return false; // key或mapKey为空
        }
        try {
            redisTemplate.opsForHash().put(key, mapKey, JSONObject.toJSONString(mapValue));
            return true;
        } catch (Exception e) {
            log.error("Error update element from map in Redis", e);
            return false;
        }
    }

    @Override
    public <T> T getElementFromMap(String key, Object mapKey, Class<T> clazz) {
        if (key == null || mapKey == null) {
            log.warn("Error get element from map in Redis: key or mapKey is null");
            return null;
        }
        try {
            String json = (String) redisTemplate.opsForHash().get(key, mapKey);
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            log.error("Error get element from map in Redis", e);
            return null;
        }
    }

    @Override
    public boolean deleteAny(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key)); // 删除指定键
        } catch (Exception e) {
            log.error("Error deleting any in Redis", e);
            return false;
        }
    }

    @Override
    public Long hashDelete(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKey); // 删除指定哈希键
        } catch (Exception e) {
            log.error("Error deleting hash value in Redis", e);
            return null;
        }
    }

    @Override
    public boolean deleteByPrefix(String prefix) {
        try {
            Set<String> keys = getKeyByPrefix(prefix); // 获取所有匹配的键
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys); // 删除所有匹配的键
            }
            return true;
        } catch (Exception e) {
            log.error("Error deleting by prefix in Redis", e);
            return false;
        }
    }

    @Override
    public Set<String> getKeyByPrefix(String prefix) {
        try {
            return redisTemplate.keys(prefix + "*"); // 获取以指定前缀开头的所有键
        } catch (Exception e) {
            log.error("Error getting key by prefix in Redis", e);
            return null; // 返回空集合
        }
    }

    @Override
    public Long incr(String key) {
        try {
            return redisTemplate.opsForValue().increment(key, 1); // 对指定键进行自增操作
        } catch (Exception e) {
            log.error("Error incrementing value in Redis", e);
            return null;
        }
    }

    @Override
    public Object getValue(String key, DataType dataType) {
        try {
            switch (dataType) {
                case STRING:
                    return redisTemplate.opsForValue().get(key); // 获取字符串类型的值
                case HASH:
                    return redisTemplate.opsForHash().entries(key); // 获取哈希类型的值
                case LIST:
                    return redisTemplate.opsForList().range(key, 0, -1); // 获取列表类型的值
                case SET:
                    return redisTemplate.opsForSet().members(key); // 获取集合类型的值
                case ZSET:
                    return redisTemplate.opsForZSet().range(key, 0, -1); // 获取有序集合类型的值
                default:
                    return null; // 不支持的类型
            }
        } catch (Exception e) {
            log.error("Error getting value from Redis", e);
            return null;
        }
    }

    @Override
    public void setCache(String key, String hashKey, DataType dataType, Object value) {
        try {
            switch (dataType) {
                case STRING:
                    redisTemplate.opsForValue().set(key, value); // 设置字符串类型的缓存
                    break;
                case HASH:
                    redisTemplate.opsForHash().put(key, hashKey, JSON.toJSONString(value)); // 设置哈希类型的缓存
                    break;
                case LIST:
                    redisTemplate.opsForList().rightPush(key, JSON.toJSONString(value)); // 设置列表类型的缓存
                    break;
                case SET:
                    redisTemplate.opsForSet().add(key, JSON.toJSONString(value)); // 设置集合类型的缓存
                    break;
                case ZSET:
                    // 假设 score 为 1.0，如果有特殊需求可传入
                    redisTemplate.opsForZSet().add(key, JSON.toJSONString(value), 1.0); // 设置有序集合类型的缓存
                    break;
                default:
                    break; // 不支持的类型
            }
        } catch (Exception e) {
            log.error("Error setting cache in Redis", e);
        }
    }

    @Override
    public List<SysCache> scanRedis(String matchKey, int pageNum, int pageSize) {
        List<SysCache> result = new ArrayList<>();
        try {
            Set<String> keys = redisTemplate.keys(matchKey + "*"); // 获取匹配的键
            if (keys != null) {
                int start = (pageNum - 1) * pageSize; // 计算起始索引
                int end = Math.min(start + pageSize, keys.size()); // 计算结束索引

                List<String> keyList = new ArrayList<>(keys);
                for (int i = start; i < end; i++) {
                    String key = keyList.get(i);
                    Object value = getValue(key, DataType.STRING); // 假设默认获取字符串类型
                    // 这里需要根据实际数据类型设置 dataType
                    DataType dataType = DataType.STRING; // 替换为实际获取的数据类型
                    result.add(new SysCache(key, value, dataType)); // 创建 SysCache 对象并添加到结果
                }
            }
        } catch (Exception e) {
            log.error("Error scanning Redis", e);
        }
        return result;
    }

    @Override
    public void releaseResource() throws Exception {
        try {
            // 关闭连接池
            Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().close();
        } catch (Exception e) {
            log.error("Error releasing Redis resources", e);
            throw new Exception("Failed to release Redis resources", e);
        }
    }

    @Override
    public void produce(String key, String message) {
        try {
            redisTemplate.opsForList().rightPush(key, message); // 向列表右侧添加消息
        } catch (Exception e) {
            log.error("Error producing message to Redis", e);
        }
    }

    @Override
    public String consume(String key) {
        try {
            return (String) redisTemplate.opsForList().leftPop(key); // 从列表左侧消费消息
        } catch (Exception e) {
            log.error("Error consuming message from Redis", e);
            return null;
        }
    }

    @Override
    public Object blockingConsume(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key, 0, TimeUnit.SECONDS); // 阻塞式消费消息
        } catch (Exception e) {
            log.error("Error consuming message from Redis", e);
            return null;
        }
    }

    @Override
    public Long getListLength(String key) {
        try {
            return redisTemplate.opsForList().size(key); // 获取列表长度
        } catch (Exception e) {
            log.error("Error getting list length from Redis", e);
            return null;
        }
    }

    //Error
    @Override
    public Set<Object> diffSet(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            log.warn("No keys provided for diff set calculation.");
            return Collections.emptySet(); // 返回空集合
        }
        try {
            Set<Object> resultSet = new HashSet<>();
            // 获取第一个集合的所有成员
            Set<Object> firstSet = redisTemplate.opsForSet().members(keys.iterator().next());
            if (firstSet != null) {
                resultSet.addAll(firstSet); // 将第一个集合的成员添加到结果集合
            }

            // 遍历剩余的集合并求差集
            for (String key : keys) {
                Set<Object> currentSet = redisTemplate.opsForSet().members(key);
                if (currentSet != null) {
                    resultSet.removeAll(currentSet); // 求差集
                }
            }
            return resultSet;
        } catch (Exception e) {
            log.error("Error getting difference set from Redis", e);
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hashSet(String key, String hashKey) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, true); // 设置哈希值，值为 true
            return true;
        } catch (Exception e) {
            log.error("Error setting hash value in Redis", e);
            return false;
        }
    }

    @Override
    public Set<Object> getHashKeys(String key) {
        try {
            return redisTemplate.opsForHash().keys(key); // 获取哈希键
        } catch (Exception e) {
            log.error("Error getting hash keys from Redis", e);
            return Collections.emptySet(); // 返回空集合
        }
    }

    @Override
    public void deleteByMatchKey(String matchKey) {
        try {
            Set<String> keys = redisTemplate.keys(matchKey); // 获取匹配的键
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys); // 删除匹配的键
            }
        } catch (Exception e) {
            log.error("Error deleting keys from Redis", e);
        }
    }

    @Override
    public List<String> listKeysByMatchKey(String matchKey) {
        List<String> matchedKeys = new ArrayList<>();
        try {
            Set<String> keys = redisTemplate.keys(matchKey); // 获取匹配的键
            if (keys != null && !keys.isEmpty()){
                matchedKeys.addAll(keys); // 添加到列表中
            }
            else {
                log.warn("No keys found matching the pattern: {}", matchKey);
            }
        } catch (Exception e) {
            log.error("Error listing keys from Redis", e);
        }
        return matchedKeys; // 返回匹配的键列表
    }

    @Override
    public Long countByMatchKey(String matchKey) {
        try {
            Set<String> keys = redisTemplate.keys(matchKey); // 获取匹配的键
            return (long) (keys != null ? keys.size() : 0); // 返回数量
        } catch (Exception e) {
            log.error("Error counting keys from Redis", e);
            return 0L; // 返回 0
        }
    }

    @Override
    public List<Object> queryListByMatchKey(String matchKey, long pageSize, long pageNum) {
        List<Object> result = new ArrayList<>();
        try {
            Set<String> keys = redisTemplate.keys(matchKey); // 获取匹配的键
            if (keys != null) {
                List<String> keyList = new ArrayList<>(keys);
                int start = (int) (pageNum - 1) * (int) pageSize; // 计算起始索引
                int end = Math.min(start + (int) pageSize, keyList.size()); // 计算结束索引

                for (int i = start; i < end; i++) {
                    result.add(redisTemplate.opsForValue().get(keyList.get(i))); // 获取每个键的值
                }
            }
        } catch (Exception e) {
            log.error("Error querying list from Redis", e);
        }
        return result; // 返回结果列表
    }
}
