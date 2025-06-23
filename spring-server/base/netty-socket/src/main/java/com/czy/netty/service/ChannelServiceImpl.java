package com.czy.netty.service;

import com.czy.api.api.socket.ChannelService;
import com.czy.netty.channel.ChannelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/2 10:27
 */
@Slf4j
@RequiredArgsConstructor
@Component
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class ChannelServiceImpl implements ChannelService {

    private final ChannelManager channelManager;

    @Override
    public boolean isOnline(Long userId) {
        if (userId == null){
            return false;
        }
        return channelManager.checkIsOnline(userId);
    }

    @Override
    public void forceOffline(Long userId) {
        if (userId == null){
            log.warn("forceOffline::userId is null");
            return;
        }
        channelManager.unRegister(userId);
    }
}
