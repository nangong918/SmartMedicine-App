package com.czy.springUtils.service;

import com.czy.springUtils.redis.RedisClusterLock;
import com.czy.springUtils.redis.SysCache;
import org.springframework.data.redis.connection.DataType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 13225
 * @date 2024/12/26 18:20
 */
public interface RedisManagerService {

    //---------------------分布式锁---------------------

    /**
     * 创建分布式锁
     *
     * @param type 锁的类型
     * @param key  锁的键
     * @return 返回锁对象
     */
    RedisClusterLock createLock(String type, String key);

    /**
     * 创建分布式锁
     *
     * @param type 锁的类型
     * @param key  锁的键
     * @param expireTime 过期时间
     * @return 返回锁对象
     */
    RedisClusterLock createLock(String type, String key, long expireTime);

    /**
     * 获取锁 redis set nx ex
     *
     * @param key key
     * @return true 如果成功获取锁（即键不存在），则返回 true；如果锁已经被其他请求占用，返回 false。
     */
    boolean getLock(final String key);

    /**
     * 获取锁 redis set nx ex，并且设置该锁的过期时间（单位为秒）。
     *
     * @param key        key
     * @param expireTime 单位是秒 而不是毫秒
     * @return  如果成功获取锁，返回 true；如果锁已经被占用，返回 false。过期时间的设置可以防止锁长时间不释放而导致的死锁情况。
     */
    boolean getLock(final String key, Long expireTime);

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

    // ---------------------Object---------------------
    boolean setObjectAsString(String key, Object obj, Long expireTimes);
    boolean setObjectAsHash(String key, Object obj, Long expireTimes);
        // 反序列化失败 异常处理
    <T> T getObjectFromString(String key, Class<T> clazz);
    <T> T getObjectFromHash(String key, Class<T> clazz);

    // ---------------------List---------------------
    boolean setObjectListAsString(String key, List<?> list, Long expireTimes);
    <T> List<T> getObjectListFromString(String key, Class<T> clazz);
    /**
     * 获取队列元素
     *
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the list
     */
    <T> List<T> range(String key, long start, long end, Class<T> clazz);
    // 新增 List 增删指定元素
    boolean addElementToList(String key, Object element);
    boolean removeElementFromList(String key, Object element);
    // ---------------------Set---------------------
    boolean setObjectSetAsString(String key, Set<?> set, Long expireTimes);
    boolean setObjectSetAsZSet(String key, Set<?> set, Double score, Long expireTimes);
    <T> Set<T> getObjectSetFromString(String key, Class<T> clazz);
    <T> Set<T> getObjectSetFromZSet(String key, Class<T> clazz);
    /**
     * 操作set 添加元素
     */
    void addSetEle(String key, long expireTime, String... values);

    /**
     * 操作set 添加元素
     */
    void addSetEle(String key, String... values);
    // 新增 Set 增删指定元素
    boolean addElementToSet(String key, Object element);
    boolean removeElementFromSet(String key, Object element);
    // 新增 ZSet 增删指定元素
    boolean addElementToZSet(String key, Object element, Double score);
    boolean removeElementFromZSet(String key, Object element);
    // ---------------------Map---------------------
    boolean setObjectMapAsHash(String key, Map<?, ?> map, Long expireTimes);
    <T, K, V> Map<K, V> getObjectMapFromHash(String key, Class<T> clazz);
    // 新增 Map 增删指定元素
    boolean addElementToMap(String key, Object mapKey, Object mapValue);
    boolean removeElementFromMap(String key, Object mapKey);
    // 新增 Map 改查指定元素
    boolean updateElementInMap(String key, Object mapKey, Object mapValue);
    <T> T getElementFromMap(String key, Object mapKey, Class<T> clazz);
    // ---------------------删除---------------------

    /**
     * 删除缓存
     *
     * @param key 缓存键
     * @return 是否删除成功
     */
    boolean deleteAny(String key);
    /**
     * 删除hash
     *
     * @param key     the key
     * @param hashKey the hash key
     * @return long
     */
    Long hashDelete(String key, String hashKey);

    /**
     * 按前缀删除缓存
     *
     * @param prefix 前缀
     */
    boolean deleteByPrefix(String prefix);

    /**
     * 获取指定前缀的所有键
     *
     * @param prefix 前缀
     * @return 以指定前缀开头的所有键
     */
    Set<String> getKeyByPrefix(String prefix);

    /**
     * 对指定键进行自增操作
     *
     * @param key 键
     * @return 自增后的值
     */
    Long incr(String key);

    // ---------------------------缓存数据操作---------------------------

    /**
     * 从缓存中获取数据，根据数据类型
     *
     * @param key      缓存键
     * @param dataType 数据类型
     * @return 缓存数据
     */
    Object getValue(String key, DataType dataType);

    /**
     * 设置缓存数据
     *
     * @param key      缓存键
     * @param secondKey 缓存的第二个键
     * @param dataType 数据类型
     * @param value    缓存数据
     */
    void setCache(String key, String secondKey, DataType dataType, Object value);

    /**
     * 分页扫描 Redis 数据
     *
     * @param matchKey 匹配的键
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 扫描到的数据列表
     */
    List<SysCache> scanRedis(String matchKey, int pageNum, int pageSize);

    // ---------------------------释放Redis资源---------------------------

    /**
     * 释放 Redis 连接资源
     *
     * @throws Exception 可能抛出异常
     */
    void releaseResource() throws Exception;

    // ---------------------------消息队列---------------------------
    /**
     * 往list队列塞入消息
     *
     * @param key     the key
     * @param message the message
     */
    void produce(String key, String message);

    /**
     * 消费list队列的消息一次
     *
     * @param key the key
     * @return the string
     */
    String consume(String key);

    /**
     * 阻塞式消费一次
     *
     * @param key the key
     * @return the object
     */
    Object blockingConsume(String key);

    /**
     * 剩余队列长度
     *
     * @param key the key
     * @return the list length
     */
    Long getListLength(String key);
    // ---------------------------其他---------------------------

    /**
     * 操作set 求差集
     */
    Set<Object> diffSet(Collection<String> keys);


    /**
     * 设置hash
     *
     * @param key     the key
     * @param hashKey the hash key
     * @return boolean
     */
    boolean hashSet(String key, String hashKey);


    /**
     * 获取所有的hashkey
     *
     * @param key the key
     * @return hash keys
     */
    Set<Object> getHashKeys(String key);

    /**
     * 根据模糊匹配键删除  例如 checkoutUrl:queue:*:*  匹配 checkoutUrl:queue:null(任意字符):0509634743(任意字符)
     *
     * @param matchKey
     */
    void deleteByMatchKey(String matchKey);

    /**
     * 查询所有模糊匹配的键
     *
     * @param matchKey 例如 checkoutUrl:queue:*:*  匹配 checkoutUrl:queue:null(任意字符):0509634743(任意字符)
     * @return {@link List}<{@link Object}>
     */
    List<String> listKeysByMatchKey(String matchKey);

    /**
     * 计数
     *
     * @param matchKey
     * @return {@link List}<{@link Object}>
     */
    Long countByMatchKey(String matchKey);

    /**
     * 分页查询所有模糊匹配键的值
     *
     * @param matchKey
     * @param pageSize
     * @param pageNum
     * @return {@link List}<{@link Object}>
     */
    List<Object> queryListByMatchKey(String matchKey, long pageSize, long pageNum);


}
