package com.czy.gateway.service.impl;

import com.czy.api.exception.GatewayExceptions;
import com.czy.gateway.constant.IpConstant;
import com.czy.gateway.service.FlowLimitService;
import com.czy.springUtils.service.RedisManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import utils.ResponseUtils;

/**
 * @author 13225
 * @date 2025/1/16 21:40
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FlowLimitServiceImpl implements FlowLimitService {

    // 在测试完成ReactiveRedis之后改为响应式redis
    private final RedisManagerService redisManagerService;

    @Deprecated
    @Override
    public Mono<Boolean> accessAndRecord(String prefix, String key) {
        return Mono.defer(() -> {
            boolean result = _accessAndRecord(prefix, key);
            return Mono.just(result);
        });

    }


    @Override
    public Mono<Void> accessAndRecord(ServerWebExchange exchange, GatewayFilterChain chain,
                                      String prefix, String key) {
        Integer whiteLimitNum = redisManagerService.getObjectFromString(IpConstant.WHITE_LIMIT_NUM + prefix, Integer.class);
        Long blackLimitTime = redisManagerService.getObjectFromString(IpConstant.BLACK_LIMIT_Time + prefix, Long.class);
        if (whiteLimitNum == null && blackLimitTime == null){
            whiteLimitNum = IpConstant.MAX_ACCESS_ATTEMPTS;
            blackLimitTime = IpConstant.BLACKLIST_EXPIRY;
            setWhiteAndBlackList(IpConstant.WHITE_LIMIT_NUM + prefix, whiteLimitNum, blackLimitTime);
        } else if (whiteLimitNum == null){
            whiteLimitNum = IpConstant.MAX_ACCESS_ATTEMPTS;
        } else if (blackLimitTime == null){
            blackLimitTime = IpConstant.BLACKLIST_EXPIRY;
        }
        // 1. 检查是否在黑名单中
        String blackListKey = IpConstant.BLACK_LIST_KEY + prefix + key;
        if (redisManagerService.hasKey(blackListKey)){
            // 存在并阻止访问
            return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.IP_ACCESS_TOO_FREQUENTLY);
        }
        // 2. 检查是否在白名单中
        String whiteListKey = IpConstant.WHITE_LIST_KEY + prefix + key;
        if (redisManagerService.hasKey(whiteListKey)){
            // 在白名单中，增加访问次数
            if (incrementAccessCount(whiteListKey) > whiteLimitNum) {
                // 超过次数限制，加入黑名单 (时间限制：秒)
                banKey(prefix, key, blackLimitTime);
            }
        }
        else {
            // 不在白名单中，添加到白名单
            redisManagerService.setObjectAsString(whiteListKey, 1, null);
        }
        return chain.filter(exchange);
    }

    @Deprecated
    private boolean _accessAndRecord(String prefix, String key) {
        Integer whiteLimitNum = redisManagerService.getObjectFromString(IpConstant.WHITE_LIMIT_NUM + prefix, Integer.class);
        Long blackLimitTime = redisManagerService.getObjectFromString(IpConstant.BLACK_LIMIT_Time + prefix, Long.class);
        if (whiteLimitNum == null && blackLimitTime == null){
            whiteLimitNum = IpConstant.MAX_ACCESS_ATTEMPTS;
            blackLimitTime = IpConstant.BLACKLIST_EXPIRY;
            setWhiteAndBlackList(IpConstant.WHITE_LIMIT_NUM + prefix, whiteLimitNum, blackLimitTime);
        } else if (whiteLimitNum == null){
            whiteLimitNum = IpConstant.MAX_ACCESS_ATTEMPTS;
        } else if (blackLimitTime == null){
            blackLimitTime = IpConstant.BLACKLIST_EXPIRY;
        }
        // 1. 检查是否在黑名单中
        String blackListKey = IpConstant.BLACK_LIST_KEY + prefix + key;
        if (redisManagerService.hasKey(blackListKey)){
            // 存在并阻止访问
            return false;
        }
        // 2. 检查是否在白名单中
        String whiteListKey = IpConstant.WHITE_LIST_KEY + prefix + key;
        if (redisManagerService.hasKey(whiteListKey)){
            // 在白名单中，增加访问次数
            if (incrementAccessCount(whiteListKey) > whiteLimitNum) {
                // 超过次数限制，加入黑名单 (时间限制：秒)
                banKey(prefix, key, blackLimitTime);
            }
        }
        else {
            // 不在白名单中，添加到白名单
            redisManagerService.setObjectAsString(whiteListKey, 1, null);
        }
        return true;
    }

    /**
     * 加入黑名单
     * @param prefix        前缀
     * @param key            key
     * @param blackLimitTime 黑名单时间
     *
     */
    private void banKey(String prefix, String key, Long blackLimitTime){
        String blackListKey = IpConstant.BLACK_LIST_KEY + prefix + key;
        String whiteListKey = IpConstant.WHITE_LIST_KEY + prefix + key;
        // 30分钟封禁
        redisManagerService.setObjectAsString(blackListKey, 1, blackLimitTime);
        // 移除白名单
        redisManagerService.deleteAny(whiteListKey);
    }

    /**
     * 增加访问次数
     * @param whiteListKey   白名单key
     * @return               当前访问次数
     */
    private int incrementAccessCount(String whiteListKey) {
        // 增加访问次数
        long accessCount = redisManagerService.incr(whiteListKey);
        // 返回当前访问次数
        return (int) accessCount;
    }

    @Override
    public void setWhiteAndBlackList(String prefix, int whiteLimit, long blackLimitTime) {
        String whiteLimitNumKey = IpConstant.WHITE_LIMIT_NUM + prefix;
        String blackLimitTimeKey = IpConstant.BLACK_LIMIT_Time + prefix;
        whiteLimit = Math.max(whiteLimit, 0);
        blackLimitTime = Math.max(blackLimitTime, 0);
        redisManagerService.setObjectAsString(whiteLimitNumKey, whiteLimit, null);
        redisManagerService.setObjectAsString(blackLimitTimeKey, blackLimitTime, null);
    }
}
