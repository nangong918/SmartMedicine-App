package com.czy.netty.config;


import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author 13225
 * @date 2025/2/5 13:52
 */


@Configuration
public class NettyConfig {
    @Value("${netty.port:66540}") // 如果未配置，则默认值为 66540
    private int port;

    /**
     * 维护用户连接
     */
    private static final ConcurrentMap<Long, Channel> userChannels = new ConcurrentHashMap<>();

    @Bean
    public Integer nettyPort() {
        return port;
    }

    @Bean
    public ConcurrentMap<Long, Channel> userChannels() {
        return userChannels;
    }

    @Value("${netty.debug:false}")
    private boolean isDebug;

    @Bean
    public boolean isDebug() {
        return isDebug;
    }
}
