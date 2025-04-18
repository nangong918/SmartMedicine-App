package com.czy.springUtils.service;


import com.czy.springUtils.redis.RedisClusterLock;
import com.czy.springUtils.redis.SysCache;
import org.springframework.data.redis.connection.DataType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * UserService服务
 * @author Administrator
 */
public interface RedisService {
    RedisClusterLock createLock(String type, String key);

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    String get(final String key);

    boolean set(final String key, String value);

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    boolean set(final String key, String value, Long expireTime);

    /**
     * 获取锁 redis set nx ex
     *
     * @param key key
     * @return
     */
    boolean getLock(final String key);

    /**
     * 获取锁 redis set nx ex
     *
     * @param key        key
     * @param expireTime 单位是秒 而不是毫秒
     * @return
     */
    boolean getLock(final String key, Long expireTime);

    /**
     * 获取所有key
     * @param keyPrefix
     * @return
     */
    Set<String> getKeys(String keyPrefix);
    /**
     * @param key
     */
    void remove(final String key);

    boolean delete(final String key);

    boolean setList(String key, List<?> list, Long expireTimes);

    boolean setList(String key, List<?> list);

    <T> List<T> getList(String key, Class<T> cls);

    /**
     * 判断缓存中是否有对应地Value
     *
     * @param key
     * @return
     */
    boolean exists(final String key);


    <T> T getObject(String key, Class<T> tClass);

    boolean setObject(String key, Object object, Long expireTimes);

    boolean setObject(String key, Object object);

    /**
     * 重置密码登出
     *
     * @param prefix redis prefix
     */
    void deleteByPrefix(final String prefix);

    Set<String> getKeyByPrefix(final String prefix);

    Long incr(String key);


    /**
     * 设置hash
     *
     * @param key     the key
     * @param hashKey the hash key
     * @return boolean
     */
    boolean hashSet(String key, String hashKey, String value);

    /**
     * 获取hash-value
     * @param key   -the key
     * @param hashKey -the hash key
     * @return
     */
    String hget(String key, String hashKey);

    /**
     * 删除hash
     *
     * @param key     the key
     * @param hashKey the hash key
     * @return long
     */
    boolean hashDelete(String key, String hashKey);
    /**
     * 获取队列元素
     *
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the list
     */
    List<String> range(String key, long start, long end);


    /**
     * 操作set 获取所有元素
     */
    Set<String> getAllSetEle(String key);

    /**
     * 分页获取redis数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<SysCache> scanRedis(String matchKey, int pageNum, int pageSize);

    /**
     * 获取缓存数据
     * @param key 缓存key
     * @param dataType 缓存类型
     * @return
     */
    Object getValue(String key, DataType dataType);

    /**
     * 添加缓存数据
     *
     * @param key      缓存Key
     * @param dataType 数据类型
     * @param value    缓存数据
     */
    void setCache(String key, String secondKey, DataType dataType, Object value);

    /**
     * List类型数据追加
     * @param key
     * @param values
     */
    void rightPushAll(String key, List<?> values);

    /**
     * set类型设置
     * @param key
     * @param values
     */
    void addAllToSet(String key, Set<?> values);

    /**
     * 获取所有hash键值对
     * @param key
     * @return
     */
    Map<Object, Object> hGetAll(String key);
}
