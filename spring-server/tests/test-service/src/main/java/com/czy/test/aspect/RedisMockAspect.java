package com.czy.test.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/9/1 16:30
 */

@Slf4j
@Aspect
@Component
public class RedisMockAspect {

    @Around("execution(* com.czy.test.service.impl.RedisAopTestServiceImpl.hitTest(..))")
    public Object simulateHit(ProceedingJoinPoint joinPoint) throws Throwable {
        // 模拟缓存命中
        String result = "hitTest: simulated hit, 命中缓存, 原先的方法不执行了";
        log.info("Cache hit for hitTest method.");
        // 返回模拟的命中结果，不执行原方法
        return result;
    }

    @Around("execution(* com.czy.test.service.impl.RedisAopTestServiceImpl.missTest(..))")
    public Object simulateMiss(ProceedingJoinPoint joinPoint) throws Throwable {
        // 模拟缓存未命中，执行原方法
        log.info("Cache miss for missTest method.");
        Object result = joinPoint.proceed();
        String resultStr = (String) result;
        log.info("result: {}", resultStr);
        return result;
    }

}
