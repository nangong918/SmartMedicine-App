package com.czy.test.service.impl;

import com.czy.test.service.RedisAopTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
