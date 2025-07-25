package com.czy.gateway.filter;

import com.czy.api.constant.gateway.InterceptorConstant;
import com.czy.api.exception.CommonExceptions;
import com.czy.gateway.service.FlowLimitService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import utils.ResponseUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/4/4 11:53
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class IpGatewayFilterFactory extends AbstractGatewayFilterFactory<IpGatewayFilterFactory.Config> {

    private final FlowLimitService flowLimitService;
    @Override
    public GatewayFilter apply(IpGatewayFilterFactory.Config config) {

        return new OrderedGatewayFilter((exchange, chain) -> {
            String ip = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                    .map(InetSocketAddress::getAddress)
                    .map(InetAddress::getHostAddress)
                    .orElse("");

            String url = exchange.getRequest().getURI().getPath();
            String key = url + ":" + ip;

            return flowLimitService.accessAndRecord(
                    exchange,
                    chain ,
                    InterceptorConstant.IP_PREFIX,
                    key
            ).onErrorResume(e -> {
                log.error("IP 限流异常", e);
                return ResponseUtils.setErrorResponse(exchange, CommonExceptions.SYSTEM_ERROR);
            });
/*            return flowLimitService.accessAndRecord(InterceptorConstant.IP_PREFIX, key)
                    .flatMap(isAllowed -> {
                        if (!isAllowed) {
                            ex.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            ex.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
                            return ex.getResponse().setComplete();
                        }
                        // 允许通过则继续
                        return chain.filter(ex);
                    });*/
        },1);
    }

    @Getter
    @Setter
    public static class Config {
        // 可以在这里添加配置属性，如果需要的话
    }
}
