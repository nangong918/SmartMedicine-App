package com.czy.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.czy.api.api.auth.TokenGeneratorService;
import com.czy.api.api.auth.TokenValidatorService;
import com.czy.api.constant.auth.JwtConstant;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.springUtils.debug.DebugConfig;
import jwt.TokenStatue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author 13225
 * @date 2025/4/4 20:54
 * TODO 存在问题：JWT是无状态认证，所以无法踢人，因为只要token正确，就能访问。没有踢人能力。
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtGatewayFilterFactory.Config> {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private TokenGeneratorService tokenGeneratorService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private TokenValidatorService tokenValidatorService;
    private final DebugConfig debugConfig;

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            if (!debugConfig.isAccessTokenCheck()) {
                return chain.filter(exchange);
            }

            String accessToken = exchange.getRequest().getHeaders().getFirst(JwtConstant.ACCESS_TOKEN_NAME);

            if (!StringUtils.hasText(accessToken)) {
                log.warn("accessToken为空");
                return setErrorResponse(exchange, "accessToken为空");
            }

            return Mono.fromCallable(() ->
                            tokenValidatorService.checkTokenStatus(accessToken, JwtConstant.ACCESS_TOKEN_GENERATE_KEY))
                    .flatMap(accountTokenStatue -> {
                        if (TokenStatue.VALID.equals(accountTokenStatue)) {
                            return chain.filter(exchange);
                        }
                        else if (TokenStatue.EFFECTIVE.equals(accountTokenStatue)) {
                            String refreshToken = exchange.getRequest().getHeaders().getFirst(JwtConstant.REFRESH_TOKEN_NAME);
                            return handleRefreshToken(exchange, chain, refreshToken);
                        }
                        else {
                            log.warn("accessToken无效");
                            return setErrorResponse(exchange, "accessToken无效");
                        }
                    })
                    .onErrorResume(e -> {
                        log.error("accessToken验证出现异常", e);
                        return setErrorResponse(exchange, "accessToken验证出现异常");
                    });
        },
                0);
    }

    private Mono<Void> handleRefreshToken(ServerWebExchange exchange, GatewayFilterChain chain, String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            log.warn("refreshToken为空");
            return setErrorResponse(exchange, "refreshToken为空");
        }

        return Mono.fromCallable(() -> tokenValidatorService.checkTokenStatus(refreshToken, JwtConstant.REFRESH_TOKEN_GENERATE_KEY))
                .flatMap(refreshTokenStatue -> {
                    if (TokenStatue.VALID.equals(refreshTokenStatue)) {
                        return Mono.fromCallable(() -> tokenValidatorService.getJwtTokenAo(refreshToken, LoginJwtPayloadAo.class))
                                .flatMap(payloadAo -> {
                                    String newAccessToken = null;
                                    try {
                                        newAccessToken = tokenGeneratorService.generateAccessToken(payloadAo);
                                    } catch (Exception e) {
                                        return setErrorResponse(exchange, "accessToken验证出现异常");
                                    }
                                    exchange.getResponse().getHeaders().set(JwtConstant.ACCESS_TOKEN_NAME, newAccessToken);
                                    return chain.filter(exchange);
                                });
                    } else if (TokenStatue.EFFECTIVE.equals(refreshTokenStatue)) {
                        log.warn("refreshToken过期，请重新登录");
                        return setErrorResponse(exchange, "refreshToken过期，请重新登录");
                    } else {
                        log.warn("refreshToken无效");
                        return setErrorResponse(exchange, "refreshToken无效");
                    }
                });
    }

    private Mono<Void> setErrorResponse(ServerWebExchange exchange, String errorMessage) {
        // 创建 BaseResponse 对象
        BaseResponse<Void> responseJson = new BaseResponse<>(HttpStatus.FORBIDDEN.value(), errorMessage, null);

        // 设置响应状态码
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);

        // 设置响应内容类型为 JSON
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 使用 FastJSON 将响应对象转为 JSON 字符串
        String jsonResponse = JSON.toJSONString(responseJson);

        // 将响应写入
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(jsonResponse.getBytes())));
    }

    @Getter
    @Setter
    public static class Config {
        // 可以在这里添加配置属性
    }

}
