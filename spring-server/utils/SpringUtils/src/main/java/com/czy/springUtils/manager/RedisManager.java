package com.czy.springUtils.manager;

import lombok.NonNull;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2024/12/26 10:43
 */

@Component// @Component可以被@Autowired注入成单例
public class RedisManager {

    // @Autowired
    @Resource
    private RedisTemplate<String, Object> redisTemplate; // 泛型类，定义为RedisTemplate<String, Object>

    //---------------------------Key---------------------------

    public class Key {
        /**
         * 给一个指定的 key 值附加过期时间
         *
         * @param key 需要设置过期时间的键
         * @param timeout 过期时间（秒）
         * @return 如果设置成功返回 true，否则返回 false
         */
        public boolean expire(final String key, final long timeout) {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
        }

        /**
         * 根据key 获取过期时间
         *
         * @param key   键
         * @return      过期时间
         */
        public long getTime(@NonNull String key) {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire == null ? -1 : expire;
        }

        /**
         * 判断 key是否存在
         *
         * @param key 键
         * @return true 存在 false不存在
         */
        public boolean hasKey(String key) {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        }

        /**
         * 移除指定key 的过期时间
         *
         * @param key   键
         * @return      true 移除成功 false 移除失败
         */
        public boolean persist(String key) {
            return Boolean.TRUE.equals(redisTemplate.boundValueOps(key).persist());
        }
    }

    //---------------------------缓存 Value---------------------------

    public class CacheValue {
        /**
         * 缓存基本的对象，Integer、String、实体类等
         *
         * @param key 缓存的键值
         * @param value 缓存的值
         */
        public <T> void setCacheValue(@NonNull final String key, final T value) {
            redisTemplate.opsForValue().set(key, value);
        }

        /**
         * 缓存基本的对象，Integer、String、实体类等
         *
         * @param key 缓存的键值
         * @param value 缓存的值
         * @param timeout 时间
         * @param timeUnit 时间颗粒度
         */
        public <T> void setCacheValue(@NonNull final String key, final T value, final long timeout, final TimeUnit timeUnit) {
            redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        }

        /**
         * 获得缓存的基本对象。
         *
         * @param key 缓存键值
         * @return 缓存键值对应的数据
         */
        @SuppressWarnings("unchecked")
        public <T> T getCacheValue(final String key) throws Exception {
            ValueOperations<String, Object> operation = redisTemplate.opsForValue();
            try {
                Object value = operation.get(key);
                // 所有获取操作存在强转都可能出错
                return value == null ? null : (T) value;
            } catch (Exception e){
                throw new Exception(e);
            }
        }

        /**
         * 删除单个对象
         *
         * @param key 缓存的键值
         */
        public boolean deleteValue(final String key) {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        }
    }

    //---------------------------缓存 List---------------------------

    public class CacheList {
        /**
         * 缓存List数据
         *
         * @param key 缓存的键值
         * @param dataList 待缓存的List数据
         * @return 缓存的对象
         */
        public <T> long setCacheList(final String key, final List<T> dataList) {
            Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
            return count == null ? 0 : count;
        }

        /**
         * 获得缓存的list对象
         *
         * @param key 缓存的键值
         * @return 缓存键值对应的数据
         */
        @SuppressWarnings("unchecked")
        public <T> List<T> getCacheList(final String key) throws Exception{
            List<Object> objectList = redisTemplate.opsForList().range(key, 0, -1);
            try {
                // 使用 Optional 处理可能的 null 值
                return Optional.ofNullable(objectList)
                        .orElseGet(Collections::emptyList) // 如果 objectList 为 null，返回空列表
                        .stream()
                        .map(o -> (T) o)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
    }

    //---------------------------缓存 Hash---------------------------

    public class CacheHash {
        // 设置 Hash 值
        public <T> void putHashValue(@NonNull String key, @NonNull String hashKey, T value) {
            redisTemplate.opsForHash().put(key, hashKey, value);
        }

        // 获取 Hash 值
        @SuppressWarnings("unchecked")
        public <T> T getHashValue(@NonNull String key, @NonNull String hashKey) {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            return (T) value; // 可能需要 @SuppressWarnings("unchecked")
        }

        // 删除 Hash 值
        public boolean deleteHashValue(@NonNull String key, @NonNull String hashKey) {
            Long delete = redisTemplate.opsForHash().delete(key, hashKey);
            return delete > 0;
        }

        // 获取所有 Hash 键值对
        public Map<Object, Object> getAllHash(@NonNull String key) {
            return redisTemplate.opsForHash().entries(key);
        }
    }

    //---------------------------缓存 Set(无序集合)---------------------------

    public class CacheSet {
        // 添加元素到 Set
        public <T> boolean addSetValue(@NonNull String key, T value) {
            Long add = redisTemplate.opsForSet().add(key, value);
            return add != null && add > 0;
        }

        // 获取 Set 中的所有元素
        @SuppressWarnings("unchecked")
        public <T> Set<T> getSetValues(@NonNull String key) throws Exception{
            Set<Object> members = redisTemplate.opsForSet().members(key);
            try {
                return Optional.ofNullable(members).
                        orElseGet(Collections::emptySet)
                        .stream()
                        .map(o -> (T) o)
                        .collect(Collectors.toSet());
            } catch (Exception e) {
                throw new Exception(e);
            }
        }

        // 判断元素是否在 Set 中
        public boolean isMember(@NonNull String key, Object value) {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        }

        // 删除 Set 中的元素
        public boolean removeSetValue(@NonNull String key, Object value) {
            Long remove = redisTemplate.opsForSet().remove(key, value);
            return remove != null && remove > 0;
        }
    }

    //---------------------------缓存 SortedSet(ZSet)---------------------------

    public class CacheSortedSet {
        // 添加元素到 Sorted Set
        public <T> void addSortedSetValue(@NonNull String key, T value, double score) {
            redisTemplate.opsForZSet().add(key, value, score);
        }

        // 获取指定范围的 Sorted Set 元素
        @SuppressWarnings("unchecked")
        public <T> Set<T> getSortedSetValues(@NonNull String key, long start, long end) throws Exception {
            Set<Object> range = redisTemplate.opsForZSet().range(key, start, end);
            try {
                // 使用 Optional 处理可能的 null 值
                return Optional.ofNullable(range)
                        .orElseGet(Collections::emptySet) // 如果 range 为 null，返回空 Set
                        .stream()
                        .map(o -> (T) o) // 类型转换
                        .collect(Collectors.toSet());
            } catch (Exception e) {
                throw new Exception(e);
            }
        }

        // 获取 Sorted Set 中的元素数
        public long getSortedSetSize(@NonNull String key) {
            Long size = redisTemplate.opsForZSet().size(key);
            return size == null ? 0 : size;
        }

        // 获取 Sorted Set 中的分数
        public Double getScore(@NonNull String key, Object value) {
            return redisTemplate.opsForZSet().score(key, value);
        }
    }

    //---------------------------释放Redis资源---------------------------
    public void releaseRedisResource() throws Exception{
        try {
            Optional.ofNullable(redisTemplate.getConnectionFactory())
                    .map(RedisConnectionFactory::getConnection)
                    .ifPresent(RedisConnection::close);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
