package com.czy.gateway.service;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author 13225
 * @date 2025/4/29 9:46
 */
public interface JwtService {

    Mono<Void> validateAccessToken(ServerWebExchange exchange, GatewayFilterChain chain);

}
