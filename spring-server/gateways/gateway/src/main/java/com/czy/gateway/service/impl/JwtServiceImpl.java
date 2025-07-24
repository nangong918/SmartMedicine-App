package com.czy.gateway.service.impl;

import com.czy.api.api.auth.TokenGeneratorService;
import com.czy.api.api.auth.TokenValidatorService;
import com.czy.api.constant.auth.JwtConstant;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.exception.GatewayExceptions;
import com.czy.gateway.service.JwtService;
import exception.AppException;
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
            return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.ACCESS_TOKEN_EMPTY);
        }
        Long userId;
        try {
            userId = getUserId(exchange);
        } catch (AppException e) {
            return ResponseUtils.setErrorResponse(exchange, e.getExceptionEnums());
        }

        try {
            tokenValidatorService.checkTokenBelongUser(accessToken, userId);
        } catch (Exception e) {
            return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.TOKEN_USER_ID_NOT_MATCH);
        }

        return Mono.fromCallable(() -> tokenValidatorService.checkTokenStatus(
                accessToken, JwtConstant.ACCESS_TOKEN_GENERATE_KEY
                    )
                )
                .flatMap(status ->
                        handleAccessTokenStatus(exchange, chain, status)
                );
    }

    private Long getUserId(ServerWebExchange exchange) throws AppException{
        String userIdStr = exchange.getRequest().getHeaders().getFirst(JwtConstant.USER_ID_HEADER_NAME);
        if (!StringUtils.hasText(userIdStr)){
            throw new AppException(GatewayExceptions.USER_ID_ERROR);
        }
        long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (Exception e) {
            throw new AppException(GatewayExceptions.USER_ID_ERROR);
        }
        return userId;
    }

    private Mono<Void> handleAccessTokenStatus(ServerWebExchange exchange, GatewayFilterChain chain, TokenStatue status) {
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
            return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.ACCESS_TOKEN_INVALID);
        }
    }


    private Mono<Void> handleRefreshToken(ServerWebExchange exchange, GatewayFilterChain chain, String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.REFRESH_TOKEN_EMPTY);
        }

        Long userId;
        try {
            userId = getUserId(exchange);
        } catch (AppException e) {
            return ResponseUtils.setErrorResponse(exchange, e.getExceptionEnums());
        }

        try {
            tokenValidatorService.checkTokenBelongUser(refreshToken, userId);
        } catch (Exception e) {
            return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.TOKEN_USER_ID_NOT_MATCH);
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
                                        GatewayExceptions.ACCESS_TOKEN_EXPIRED_AND_REFRESH_TOKEN_INVALID
                                );
                            }
                            String newAccessToken;
                            try {
                                newAccessToken = tokenGeneratorService.generateAccessToken(payloadAo);
                            } catch (Exception e) {
                                return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.ACCESS_TOKEN_VERIFY_ERROR);
                            }
                            exchange.getResponse().getHeaders().set(JwtConstant.ACCESS_TOKEN_NAME, newAccessToken);
                            return chain.filter(exchange);
                        });
                    } else if (TokenStatue.EFFECTIVE.equals(refreshStatus)) {
                        log.warn("refreshToken过期，请重新登录");
                        return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.REFRESH_TOKEN_EXPIRED);
                    } else {
                        log.warn("refreshToken无效");
                        return ResponseUtils.setErrorResponse(exchange, GatewayExceptions.REFRESH_TOKEN_INVALID);
                    }
                });
    }


}
