package com.czy.gateway.service.impl;

import com.czy.api.api.auth.TokenGeneratorService;
import com.czy.api.api.auth.TokenValidatorService;
import com.czy.api.constant.auth.JwtConstant;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.gateway.service.JwtService;
import jwt.TokenStatue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import utils.ResponseUtils;

/**
 * @author 13225
 * @date 2025/4/29 9:47
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private TokenGeneratorService tokenGeneratorService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private TokenValidatorService tokenValidatorService;
    @Override
    public Mono<Void> validateAccessToken(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = exchange.getRequest().getHeaders().getFirst(
                JwtConstant.ACCESS_TOKEN_NAME
        );
        if (!StringUtils.hasText(accessToken)) {
            log.warn("accessToken为空");
            return ResponseUtils.setErrorResponse(exchange, "accessToken为空");
        }

        return Mono.fromCallable(() -> tokenValidatorService.checkTokenStatus(
                accessToken, JwtConstant.ACCESS_TOKEN_GENERATE_KEY
                ))
                .flatMap(status -> handleTokenStatus(exchange, chain, status));
    }

    private Mono<Void> handleTokenStatus(ServerWebExchange exchange, GatewayFilterChain chain, TokenStatue status) {
        if (TokenStatue.VALID.equals(status)) {
            return chain.filter(exchange);
        }
        else if (TokenStatue.EFFECTIVE.equals(status)) {
            String refreshToken = exchange.getRequest().getHeaders().getFirst(
                    JwtConstant.REFRESH_TOKEN_NAME
            );
            return handleRefreshToken(exchange, chain, refreshToken);
        }
        else {
            log.warn("accessToken无效");
            return ResponseUtils.setErrorResponse(exchange, "accessToken无效");
        }
    }


    private Mono<Void> handleRefreshToken(ServerWebExchange exchange, GatewayFilterChain chain, String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            log.warn("refreshToken为空");
            return ResponseUtils.setErrorResponse(exchange, "refreshToken为空");
        }

        return Mono.fromCallable(() -> tokenValidatorService.checkTokenStatus(refreshToken, JwtConstant.REFRESH_TOKEN_GENERATE_KEY))
                .flatMap(refreshStatus -> {
                    if (TokenStatue.VALID.equals(refreshStatus)) {
                        return Mono.fromCallable(() -> {
                            try {
                                return tokenValidatorService.getJwtTokenAo(
                                        refreshToken,
                                        LoginJwtPayloadAo.class
                                );
                            } catch (Exception e) {
                                // jwt解析payload失败
                                return null;
                            }
                        }).flatMap(payloadAo -> {
                            if (payloadAo == null) {
                                return ResponseUtils.setErrorResponse(
                                        exchange,
                                        "accessToken失效且refreshToken解析失败无法获得JwtPayload"
                                );
                            }
                            String newAccessToken;
                            try {
                                newAccessToken = tokenGeneratorService.generateAccessToken(payloadAo);
                            } catch (Exception e) {
                                return ResponseUtils.setErrorResponse(exchange, "accessToken验证出现异常");
                            }
                            exchange.getResponse().getHeaders().set(JwtConstant.ACCESS_TOKEN_NAME, newAccessToken);
                            return chain.filter(exchange);
                        });
                    } else if (TokenStatue.EFFECTIVE.equals(refreshStatus)) {
                        log.warn("refreshToken过期，请重新登录");
                        return ResponseUtils.setErrorResponse(exchange, "refreshToken过期，请重新登录");
                    } else {
                        log.warn("refreshToken无效");
                        return ResponseUtils.setErrorResponse(exchange, "refreshToken无效");
                    }
                });
    }


}
