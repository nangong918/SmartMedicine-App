package com.utils.mvc.redisson;


import java.util.HashMap;

/**
 * @author 13225
 * @date 2025/3/29 15:52
 */
public interface RedissonService {

    //---------------------分布式锁---------------------

    boolean tryLock(RedissonClusterLock redisLock);

    void unlock(RedissonClusterLock redisLock);

    //---------------------Key---------------------
    /**
     * 设置键的过期时间
     *
     * @param key     键
     * @param timeout 过期时间（秒）
     * @return 是否成功设置过期时间
     */
    boolean expireKey(String key, long timeout);

    /**
     * 获取指定键的剩余过期时间
     *
     * @param key 键
     * @return 剩余过期时间（秒）
     * @throws Exception 键不存在的情况
     */
    long getExpireKey(String key) throws Exception;

    /**
     * 检查指定键是否存在
     *
     * @param key 键
     * @return true 如果存在，false 如果不存在
     */
    boolean hasKey(String key);

    /**
     * 移除指定键的过期时间
     *
     * @param key 键
     * @throws Exception 键不存在的情况
     * @return 是否成功移除过期时间
     */
    boolean persist(String key) throws Exception;

    //---------------------Object---------------------

    /**
     * 设置对象
     * @param key
     * @param obj
     * @param expireTimes
     * @return
     */
    boolean setObjectByJson(String key, Object obj, Long expireTimes);

    /**
     * 设置对象
     * @param key
     * @param obj
     * @param expireTimes
     * @return
     */
    boolean setObjectBySerializable(String key, Object obj, Long expireTimes);

    /**
     * 获取对象
     * @param key
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T getObjectFromJson(String key, Class<T> clazz);

    /**
     * 获取对象
     * @param key
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T getObjectFromSerializable(String key, Class<T> clazz);

    // 删除对象和key
    boolean deleteObject(String key);

    //---------------------Hash---------------------
    void saveHashMap(String key, HashMap<String, String> data, Long expireTimes);
    void saveObjectHaseMap(String key, HashMap<String, Object> data, Long expireTimes);
    HashMap<String, String> getHashMap(String key);
    HashMap<String, Object> getObjectHaseMap(String key);
    void updateHashMap(String hashKey, String field, String value);
    void deleteHashMap(String redisKey); // 删除整个 Hash
    void deleteFieldFromHash(String redisKey, String hashKey); // 删除 Hash 中的某个字段
}
