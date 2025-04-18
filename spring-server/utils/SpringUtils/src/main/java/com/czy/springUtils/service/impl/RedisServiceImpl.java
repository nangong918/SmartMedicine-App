package com.czy.springUtils.service.impl;


import com.alibaba.fastjson.JSON;
import com.czy.springUtils.redis.RedisClusterLock;
import com.czy.springUtils.redis.SysCache;
import com.czy.springUtils.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lwl
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    private static final Long EXPIRE_SECONDS = 3600L;
    @Resource
    private  RedisTemplate<String, Object> redisTemplate;

    @Override
    public RedisClusterLock createLock(String type, String key) {
        return new RedisClusterLock(type + "_" + key, redisTemplate);
    }

    @Override
    public String get(String key) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] value = connection.get(serializer.serialize(key));
            return serializer.deserialize(value);
        });
    }

    @Override
    public boolean set(final String key, final String value) {
        return setFiled(key, value, EXPIRE_SECONDS);
    }

    @Override
    public boolean set(String key, String value, Long expireTime) {
        return setFiled(key, value, expireTime);
    }

    /**
     * 获取锁， 默认 30 s
     *
     * @param key key
     * @return boolean
     */
    @Override
    public boolean getLock(final String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, 1, Duration.ofSeconds(30)));
    }

    /**
     * 获取锁
     *
     * @param key        key
     * @param expireTime 单位是秒 而不是毫秒
     * @return boolean
     */
    @Override
    public boolean getLock(final String key, Long expireTime) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, 1, Duration.ofSeconds(expireTime)));
    }

    @Override
    public Set<String> getKeys(String keyPrefix) {
        Set<String> keys = new HashSet<>();

        // 获取所有键并过滤
        Set<String> allKeys = redisTemplate.keys(keyPrefix + "*");
        if (allKeys != null) {
            keys.addAll(allKeys);
        }

        return keys;
    }

    private boolean setFiled(final String key, String value, Long expireTime) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] serialize = serializer.serialize(key);
            redisConnection.set(serialize, serializer.serialize(value));
            redisConnection.expire(serialize, expireTime);
            return true;
        }));
    }

    @Override
    public void remove(String key) {
        Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            return connection.del(serializer.serialize(key));
        });
    }

    @Override
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public boolean setList(String key, List<?> list, Long expireTimes) {
        String json = JSON.toJSONString(list);
        return set(key, json, expireTimes);
    }


    @Override
    public boolean setList(String key, List<?> list) {
        return setList(key, list, EXPIRE_SECONDS);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> cls) {
        String json = get(key);
        if (Objects.nonNull(json)) {
            return JSON.parseArray(json, cls);
        }
        return null;
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            return connection.exists(serializer.serialize(key));
        }));
    }

    @Override
    public <T> T getObject(String key, Class<T> tClass) {
        if (key == null) {
            return null;
        }
        String json = get(key);
        if (Objects.nonNull(json)) {
            return JSON.parseObject(json, tClass);
        }
        return null;
    }

    @Override
    public boolean setObject(String key, Object object, Long expireTimes) {
        String json = JSON.toJSONString(object);
        return set(key, json, expireTimes);
    }

    @Override
    public boolean setObject(String key, Object object) {
        String json = JSON.toJSONString(object);
        return set(key, json);
    }
    @Override
    public void deleteByPrefix(String prefix) {
        String matchKey = prefix + "*";
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> binaryKeys = new HashSet<>();
            Cursor<byte[]> cursor =
                    connection.scan(new ScanOptions.ScanOptionsBuilder().match(matchKey).count(50).build());
            while (cursor.hasNext()) {
                binaryKeys.add(new String(cursor.next()));
            }
            return binaryKeys;
        });
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            remove(key);
        }
    }

    @Override
    public Set<String> getKeyByPrefix(String prefix) {
        String matchKey = prefix + "*";
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> binaryKeys = new HashSet<>();
            Cursor<byte[]> cursor =
                    connection.scan(new ScanOptions.ScanOptionsBuilder().match(matchKey).count(50).build());
            while (cursor.hasNext()) {
                binaryKeys.add(new String(cursor.next()));
            }
            return binaryKeys;
        });
    }

    @Override
    public Long incr(String key) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            return connection.incr(Objects.requireNonNull(serializer.serialize(key)));
        });

    }

    @Override
    public boolean hashSet(String key, String hashKey, String value) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            try {
                return connection.hSet(key.getBytes(), hashKey.getBytes(), value.getBytes());
            } catch (Exception e) {
                log.error("Error setting hash value in Redis", e);
                return false;
            }
        }));
    }

    @Override
    public String hget(String key, String hashKey) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            try {
                byte[] rawValue = connection.hGet(key.getBytes(), hashKey.getBytes());
                return rawValue != null ? new String(rawValue) : null;
            } catch (Exception e) {
                log.error("Error getting hash value from Redis", e);
                return null;
            }
        });
    }

    @Override
    public boolean hashDelete(String key, String hashKey) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            try {
                long result = connection.hDel(key.getBytes(), hashKey.getBytes());
                return result > 0;
            } catch (Exception e) {
                log.error("Error deleting hash value from Redis", e);
                return false;
            }
        }));
    }

    /**
     * 获取队列元素
     *
     * @param key
     */
    @Override
    public List<String> range(String key, long start, long end) {
        return redisTemplate.execute((RedisCallback<List<String>>) connection -> {
            try {
                byte[] rawKey = key.getBytes();
                List<byte[]> rawValues = connection.lRange(rawKey, start, end);
                List<String> values = new ArrayList<>();
                if (rawValues != null) {
                    for (byte[] rawValue : rawValues) {
                        values.add(new String(rawValue));
                    }
                }
                return values;
            } catch (Exception e) {
                log.error("Error getting range from Redis list", e);
                return Collections.emptyList();
            }
        });
    }


    @Override
    public Set<String> getAllSetEle(String key) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            try {
                byte[] rawKey = key.getBytes();
                Set<byte[]> rawMembers = connection.sMembers(rawKey);
                Set<String> members = new HashSet<>();
                if (rawMembers != null) {
                    for (byte[] rawMember : rawMembers) {
                        members.add(new String(rawMember));
                    }
                }
                return members;
            } catch (Exception e) {
                log.error("Error getting all set elements from Redis", e);
                return Collections.emptySet();
            }
        });
    }

    public List<SysCache> scanRedis(String matchKey, int pageNum, int pageSize) {
        List<SysCache> result = new ArrayList<>();
        long skipCount = (long) (pageNum - 1) * pageSize;

        redisTemplate.execute((RedisCallback<Object>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(pageSize).match(matchKey + "*").build())) {
                long count = 0;
                while (cursor.hasNext()) {
                    byte[] nextKey = cursor.next();
                    if (nextKey != null && count >= skipCount && count < skipCount + pageSize) {
                        // 获取key是什么数据类型
                        result.add(new SysCache(new String(nextKey), connection.type(nextKey)));
                    }
                    count++;
                }
            } catch (Exception e) {
                log.error("Error scanning Redis keys", e);
            }
            return null;
        });

        return result;
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key must not be null or empty");
        }

        RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();

        return redisTemplate.execute((RedisCallback<Map<Object, Object>>) connection -> {
            Map<byte[], byte[]> rawMap = connection.hGetAll(key.getBytes());
            if (rawMap == null) {
                return Collections.emptyMap();
            }
            return rawMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> new String(entry.getKey()),
                            entry -> new String(entry.getValue())
                    ));
        });
    }




    @Override
    public Object getValue(String key, DataType dataType) {
        switch (dataType) {
            case STRING:
                return get(key);
            case LIST:
                return range(key, 0, -1);
            case SET:
                return getAllSetEle(key);
            case ZSET:
                return redisTemplate.opsForZSet().range(key, 0, -1);
            case HASH:
                return hGetAll(key);
            default:
                throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
    }

    @Override
    public void setCache(String key, String secondKey, DataType dataType, Object value) {
        switch (dataType) {
            case STRING:
                if (value instanceof String) {
                    this.set(key, String.valueOf(value));
                } else {
                    this.setObject(key, value, -1L);
                }
                // redisTemplate.opsForValue().set(key, value);
                break;
            case LIST:
                if (value instanceof List) {
                    rightPushAll(key, (List<?>) value);
                } else {
                    throw new IllegalArgumentException("Value must be a List for LIST data type");
                }
                break;
            case SET:
                if (value instanceof Set) {
                    addAllToSet(key, ((Set<?>) value));
                } else {
                    throw new IllegalArgumentException("Value must be a Set for SET data type");
                }
                break;
            case ZSET:
                if (value instanceof Set) {
                    redisTemplate.opsForZSet().add(key, (Set<ZSetOperations.TypedTuple<Object>>) value);
                } else {
                    throw new IllegalArgumentException("Value must be a Set of Map.Entry for ZSET data type");
                }
                break;
            case HASH:
                if (value instanceof String) {
                    hashSet(key, secondKey, String.valueOf(value));
                } else {
                    hashSet(key, secondKey, JSON.toJSONString(value));
                }
                // redisTemplate.opsForHash().putAll(key, (Map<?, ?>) value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
    }


    @Override
    public void rightPushAll(String key, List<?> values) {
        redisTemplate.execute((RedisConnection connection) -> {
            try {
                byte[] rawKey = key.getBytes();
                List<byte[]> rawValues = values.stream()
                        .map(value -> value.toString().getBytes())
                        .collect(Collectors.toList());
                connection.rPush(rawKey, rawValues.toArray(new byte[0][]));
            } catch (Exception e) {
                log.error("Error pushing all elements to Redis list", e);
            }
            return null; // 返回类型为 void
        });
    }

    @Override
    public void addAllToSet(String key, Set<?> values) {
        redisTemplate.execute((RedisConnection connection) -> {
            try {
                byte[] rawKey = key.getBytes();
                Set<byte[]> rawValues = values.stream()
                        .map(value -> value.toString().getBytes())
                        .collect(Collectors.toSet());
                connection.sAdd(rawKey, rawValues.toArray(new byte[0][]));
            } catch (Exception e) {
                log.error("Error adding elements to Redis set", e);
            }
            return null; // 返回类型为 void
        });
    }


}
