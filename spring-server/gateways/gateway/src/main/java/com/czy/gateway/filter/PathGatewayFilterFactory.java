package com.czy.gateway.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/5/29 14:25
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PathGatewayFilterFactory extends AbstractGatewayFilterFactory<PathGatewayFilterFactory.Config> {

    // order 0级别
    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> chain.filter(exchange), 10);
    }

    @Getter
    @Setter
    public static class Config {
        // 可以在这里添加配置属性
    }
}
