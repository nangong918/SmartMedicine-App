package com.czy.gateway.filter;

import com.czy.api.exception.CommonExceptions;
import com.czy.gateway.service.JwtService;
import com.czy.springUtils.debug.DebugConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import utils.ResponseUtils;

/**
 * @author 13225
 * @date 2025/4/4 20:54
 * TODO 存在问题：JWT是无状态认证，所以无法踢人，因为只要token正确，就能访问。没有踢人能力。
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtGatewayFilterFactory.Config> {

    private final JwtService jwtService;
    private final DebugConfig debugConfig;

    // order 0级别
    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            if (!debugConfig.isAccessTokenCheck()) {
                return chain.filter(exchange);
            }

            return jwtService.validateAccessToken(exchange, chain)
                    .onErrorResume(e -> {
                        log.error("accessToken验证出现异常", e);
                        return ResponseUtils.setErrorResponse(exchange, CommonExceptions.SYSTEM_ERROR);
                    });
        }, 0);
    }

    @Getter
    @Setter
    public static class Config {
        // 可以在这里添加配置属性
    }

}
