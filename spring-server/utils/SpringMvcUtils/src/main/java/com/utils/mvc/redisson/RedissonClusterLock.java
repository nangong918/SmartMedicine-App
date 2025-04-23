package com.utils.mvc.redisson;


import lombok.Getter;

/**
 * @author 13225
 * @date 2025/4/21 14:00
 */
@Getter
public class RedissonClusterLock {
    // 锁的唯一标识
    private String id;
    // 锁的ID
    private String lockId;
    // 锁定失效时间
    private long lockTimeout;

    public RedissonClusterLock(){
    }

    public RedissonClusterLock(String dataId, String mappingPath){
        this.id = mappingPath + ":" + dataId;
        // 默认10秒之后自动锁定失效时间
        this.lockTimeout = 10L;
    }

    public RedissonClusterLock(String dataId, String mappingPath, long lockTimeout){
        this.id = mappingPath + ":" + dataId;
        if (lockTimeout < 0){
            lockTimeout = 1L;
        }
        this.lockTimeout = lockTimeout;
    }

}
