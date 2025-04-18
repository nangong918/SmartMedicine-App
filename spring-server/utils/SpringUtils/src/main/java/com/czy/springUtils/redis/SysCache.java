package com.czy.springUtils.redis;


import lombok.Data;
import org.springframework.data.redis.connection.DataType;

/**
 * 缓存信息
 * 
 * @author Jonathan
 */

@Data
public class SysCache {
    /**
     * 缓存键名
     */
    private String cacheKey = "";

    /**
     * 缓存内容
     */
    private Object cacheValue = "";

    /**
     * 数据类型
     */
    private DataType dataType;

    public SysCache() {
    }

    public SysCache(String cacheKey, DataType dataType) {
        this.cacheKey = cacheKey;
        this.dataType = dataType;
    }

    public SysCache(String cacheKey, Object cacheValue, DataType dataType) {
        this.cacheKey = cacheKey;
        this.cacheValue = cacheValue;
        this.dataType = dataType;
    }

}
