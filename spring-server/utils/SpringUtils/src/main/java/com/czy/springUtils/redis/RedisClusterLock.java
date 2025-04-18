package com.czy.springUtils.redis;

import com.czy.springUtils.manager.AbstractRedisTemplate;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class RedisClusterLock extends AbstractRedisTemplate<String> implements Lock {

    // 存储到redis中的锁标志
    private static final String LOCKED = "LOCKED";

    // 请求锁的超时时间(ms)
    private static final long TIME_OUT = 300_000L;

    // 锁的有效时间(s)
    public static final int EXPIRE = 100;

    // 锁标志对应的key;
    @Getter
    private final String key;

    // state flag
    private volatile boolean isLocked = false;

    public RedisClusterLock(String key, RedisTemplate<String, Object> redisTemplate) {
        this.key = key;
        setRedisTemplate(redisTemplate);
    }

    public RedisClusterLock(String key) {
        this.key = key;
    }

    @Override
    public void lock() {
        // 请求锁超时时间，纳秒
        long nowTime = System.nanoTime();
        // 请求锁超时时间，纳秒
        long timeout = TIME_OUT * 1000_000L;
        final Random r = new Random();
        // 不断循环向Master节点请求锁，当请求时间(System.nanoTime() - nano)超过设定的超时时间则放弃请求锁
        // 这个可以防止一个客户端在某个宕掉的master节点上阻塞过长时间
        // 如果一个master节点不可用了，应该尽快尝试下一个master节点
        while ((System.nanoTime() - nowTime) < timeout) {

            boolean lockNx = setLock(key);
            if (lockNx) {
                isLocked = true;
                // 上锁成功结束请求
                break;
            }
            // 获取锁失败时，应该在随机延时后进行重试，避免不同客户端同时重试导致谁都无法拿到锁的情况出现
            // 睡眠3毫秒后继续请求锁
            try {
                Thread.sleep(6, r.nextInt(1000));
            } catch (Exception ignored) {
            }
        }
    }

    private Boolean setLock(String key) {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();

        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            if (Boolean.TRUE.equals(
                    redisConnection.setNX(
                            Objects.requireNonNull(serializer.serialize(key)),
                            Objects.requireNonNull(serializer.serialize(key))))) {
                redisConnection.expire(Objects.requireNonNull(serializer.serialize(key)), getExpireSecond());
                return true;
            }
            return false;
        }));
    }

    public void unlock(RedisClusterLock lock) {
        boolean isLocked = lock.isLocked();

        Callable<Object> c = new TaskUnlock(lock.getKey());

        // 释放锁
        // 不管请求锁是否成功，只要已经上锁，客户端都会进行释放锁的操作
        if (isLocked) {
            try {
//				delete(key);
                unLock(lock.getKey());
//				Future<Boolean> future = RedisServiceImpl.pool.submit(c);
            } catch (Exception ignored) {
            }
        }
    }

    private void unLock(String key) {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        boolean result = Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            redisConnection.del(serializer.serialize(key));
            return true;
        }));
    }

    class TaskWithResultNx implements Callable<Boolean> {
        private final String key;

        public TaskWithResultNx(String key) {
            this.key = key;
        }

        @Override
        public Boolean call() throws Exception {
            return setIfAbsent(key, LOCKED.getBytes());
        }

    }

    class TaskWithResultExpire implements Callable<Boolean> {
        private final String key;

        public TaskWithResultExpire(String key) {
            this.key = key;
        }

        @Override
        public Boolean call() throws Exception {
            return expire(key);

        }

    }

    class TaskUnlock implements Callable<Object> {
        private final String key;

        public TaskUnlock(String key) {
            this.key = key;
        }

        @Override
        public Object call() throws Exception {
            delete(key);
            return new Object();
        }

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @NotNull
    @Override
    public Condition newCondition() {
        return null;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public void unlock() {

//		Callable c = new TaskUnlock(key);
        // 释放锁
        // 不管请求锁是否成功，只要已经上锁，客户端都会进行释放锁的操作
        if (isLocked) {
            try {
                unLock(getKey());
//				delete(key);
            } catch (Exception ignored) {
            }
        }

    }

    @Override
    protected String getKeyType() {
        return "";
    }

    @Override
    protected long getExpireSecond() {
        return 100;
    }

}