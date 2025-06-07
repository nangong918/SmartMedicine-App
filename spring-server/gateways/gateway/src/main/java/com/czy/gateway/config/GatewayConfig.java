package com.czy.gateway.config;

import com.czy.api.constant.message.MessageConstant;
import com.czy.api.constant.user_relationship.RelationshipConstant;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.gateway.filter.IpGatewayFilterFactory;
import com.czy.gateway.filter.JwtGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 13225
 * @date 2025/4/4 11:59
 */

@Configuration
public class GatewayConfig {

    // 我已经关闭了nacos的自动服务发现，使用的是lb://serviceId这种形式的路由转发。
    // 具体来说：http://localhost:8888/user-relationship-service/login/sendSms进入网关
    // 被gateway的路由规则：/user-relationship-service/login/**发现
    // 然后拼接：lb://serviceId + /user-relationship-service/login/**
    // 但是由于重复的应用名所以需要截断：.stripPrefix(1)
    // 实际上此块应该配置在配置文件中而不是写死在Java代码
    // 优先级 0
    @Bean
    public RouteLocator ipRouteLocator(RouteLocatorBuilder builder, IpGatewayFilterFactory ipGatewayFilterFactory) {
        return builder.routes()
                .route(r -> r.path(UserConstant.loginIpInterceptedURL)
                        .filters(f -> f
                                .filter(ipGatewayFilterFactory.apply(new IpGatewayFilterFactory.Config()))
                                .stripPrefix(1)// 移除第一段路径
                        )
                                // 负载均衡访问user-relationship-service服务
                        .uri(UserConstant.serviceUri)
                        .id("login_ip_route")
                )
                .build();
    }

    // 优先级 1
    @Bean
    public RouteLocator jwtRouteLocator(RouteLocatorBuilder builder, JwtGatewayFilterFactory jwtGatewayFilterFactory) {
        return builder.routes()
                .route(r -> r.path(UserConstant.loginJwtInterceptedURL)
                        .filters(f -> f.filter(jwtGatewayFilterFactory.apply(new JwtGatewayFilterFactory.Config()))
                                       .stripPrefix(1)
                        )
                        .uri(UserConstant.serviceUri)
                        .id("login_jwt_route")
                )
                .route(r -> r.path(MessageConstant.serviceRoute + MessageConstant.Chat_CONTROLLER + "/**")
                        .filters(f -> f
                                .filter(jwtGatewayFilterFactory.apply(new JwtGatewayFilterFactory.Config()))
                                .stripPrefix(1)
                        )
                        .uri(MessageConstant.serviceUri)
                        .id("chat_jwt_route")
                )
                .route(r -> r.path(MessageConstant.serviceRoute + MessageConstant.WebRTC_CONTROLLER + "/**")
                        .filters(f -> f.filter(jwtGatewayFilterFactory.apply(new JwtGatewayFilterFactory.Config()))
                                       .stripPrefix(1)
                        )
                        .uri(MessageConstant.serviceUri)
                        .id("webrtc_jwt_route")
                )
                .route(r -> r.path(RelationshipConstant.serviceRoute + RelationshipConstant.Relationship_CONTROLLER + "/**")
                        .filters(f -> f.filter(jwtGatewayFilterFactory.apply(new JwtGatewayFilterFactory.Config()))
                                       .stripPrefix(1)
                        )
                        .uri(RelationshipConstant.serviceUri)
                        .id("relationship_jwt_route"))
                .build();
    }

}
