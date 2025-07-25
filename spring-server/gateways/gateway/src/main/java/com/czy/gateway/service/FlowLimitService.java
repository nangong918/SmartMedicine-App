package com.czy.gateway.service;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author 13225
 * @date 2025/1/16 21:17
 * 限流服务
 */
public interface FlowLimitService {

    /**
     * 访问以及记key录访问次数 返回是否被限制
     * @param prefix    前缀
     * @param key       key
     * @return           是否被限制
     */
    @Deprecated
    Mono<Boolean> accessAndRecord(String prefix, String key);

    /**
     * 访问以及记key录访问次数 返回是否被限制
     * @param exchange  exchange
     * @param chain     chain
     * @param prefix    前缀
     * @param key       key
     * @return          是否被限制
     */
    Mono<Void> accessAndRecord(ServerWebExchange exchange, GatewayFilterChain chain,
                               String prefix, String key);

    /**
     * 设置某个前缀的白名单访问次数，黑名单限制时间
     * @param prefix        前缀
     * @param whiteLimit    白名单访问次数
     * @param blackLimitTime     黑名单限制时间
     */
    void setWhiteAndBlackList(String prefix, int whiteLimit, long blackLimitTime);
}
