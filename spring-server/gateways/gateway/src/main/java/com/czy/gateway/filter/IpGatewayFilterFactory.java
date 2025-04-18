package com.czy.gateway.filter;

import com.czy.api.constant.gateway.InterceptorConstant;
import com.czy.gateway.service.FlowLimitService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
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

        return new OrderedGatewayFilter((exchange, chain) ->
                Optional.ofNullable(exchange)
                .map(ex -> {
                    String ip = Optional.ofNullable(ex.getRequest().getRemoteAddress())
                            .map(InetSocketAddress::getAddress)
                            .map(InetAddress::getHostAddress)
                            .orElse("");

                    String url = ex.getRequest().getURI().getPath();
                    String key = url + ":" + ip;

                    return flowLimitService.accessAndRecord(InterceptorConstant.IP_PREFIX, key)
                            .flatMap(isAllowed -> {
                                if (!isAllowed) {
                                    ex.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                    ex.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
                                    return ex.getResponse().setComplete();
                                }
                                // 允许通过则继续
                                return chain.filter(ex);
                            });
                })
                // 如果 exchange 为 null，则返回一个空的 Mono
                .orElse(Mono.empty()),
                1);
    }

    @Getter
    @Setter
    public static class Config {
        // 可以在这里添加配置属性，如果需要的话
    }
}
