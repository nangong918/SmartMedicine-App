package com.czy.test.service.impl;

import com.czy.test.service.RedisAopTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/9/1 16:25
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisAopTestServiceImpl implements RedisAopTestService {

    @Override
    public String hitTest(@NotNull Long userId){
        return "hitTest:" + userId;
    }

    @Override
    public String missTest(@NotNull Long userId){
        return "missTest:" + userId;
    }

    @Override
    public String updateRedisTest(@NotNull Long userId){
        log.info("开始更新数据库: {}", userId);

        updateRedis(userId);

        return "数据库更新完毕: " + userId;
    }

    @Async
    public void updateRedis(Long userId){
        log.info("开始更新redis: {}", userId);
    }
}
