package com.czy.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * @author 13225
 * @date 2025/7/21 16:39
 * 失败的代码：Spring Cloud Gateway 本身是用于路由和转发请求的，通常情况下，它不直接处理域名到 IP 的映射。
 * 一般是http://localhost:8888/api/users -> http://xxx
 * 只有localhost:8888，路由到这个服务的才能转为域名。
 * 场景更适合使用Nginx而不是Spring Cloud Gateway。
 */

@Slf4j
@RequiredArgsConstructor
//@Component
public class IpToDnsGlobalFilter implements GlobalFilter, Ordered {

    private final Environment environment;
    private static final String DNS = "mydns";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI originalUri = exchange.getRequest().getURI();
        String port = environment.getProperty("server.port", "8888");

        String ip;
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
            ip = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取本机ip异常", e);
            ip = "127.0.0.1";
        }
        String[] serverIps = {
                ip,
                "127.0.0.1",
                "localhost"
        };

        // 检查当前请求是否需要重写
        boolean needRewrite = false;
        String originalHost = originalUri.getHost();
        int originalPort = originalUri.getPort();

        // 检查主机名是否在需要替换的列表中
        for (String serverIp : serverIps) {
            if (serverIp.equals(originalHost)) {
                needRewrite = true;
                break;
            }
        }

        // 检查端口是否匹配（如果URI中有端口）
        if (originalPort != -1 && !port.equals(String.valueOf(originalPort))) {
            needRewrite = false;
        }

        // 如果不需要重写，直接放行
        if (!needRewrite) {
            return chain.filter(exchange);
        }

        // 构建新的URI
        String newUriStr = getNewUriStr(originalPort, originalUri, originalHost);

        // 创建新的URI对象
        URI newUri = URI.create(newUriStr);

        // 创建修改后的exchange并继续过滤器链
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .uri(newUri)
                        .build())
                .build();

        return chain.filter(modifiedExchange);
    }

    @NotNull
    private static String getNewUriStr(int originalPort, URI originalUri, String originalHost) {
        String newUriStr;
        if (originalPort == -1) {
            // 无端口情况
            newUriStr = originalUri.toString()
                    .replace("http://" + originalHost, "http://" + DNS)
                    .replace("https://" + originalHost, "https://" + DNS);
        }
        else {
            // 有端口情况
            newUriStr = originalUri.toString()
                    .replace("http://" + originalHost + ":" + originalPort, "http://" + DNS)
                    .replace("https://" + originalHost + ":" + originalPort, "https://" + DNS);
        }
        return newUriStr;
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
