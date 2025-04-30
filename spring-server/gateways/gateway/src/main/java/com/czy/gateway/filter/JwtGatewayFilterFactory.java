package com.czy.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.czy.api.api.auth.TokenGeneratorService;
import com.czy.api.api.auth.TokenValidatorService;
import com.czy.api.constant.auth.JwtConstant;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.gateway.service.JwtService;
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
                        return ResponseUtils.setErrorResponse(exchange, "accessToken验证出现异常");
                    });
        }, 0);
    }

    @Getter
    @Setter
    public static class Config {
        // 可以在这里添加配置属性
    }

}
