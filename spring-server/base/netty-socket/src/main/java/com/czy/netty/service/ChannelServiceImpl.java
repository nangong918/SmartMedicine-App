package com.czy.netty.service;

import com.czy.api.api.socket.ChannelService;
import com.czy.netty.channel.ChannelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    public boolean isOnline(String userAccount) {
        if (!StringUtils.hasText(userAccount)){
            return false;
        }
        return channelManager.checkIsOnline(userAccount);
    }

    @Override
    public void forceOffline(String userAccount) {
        if (!StringUtils.hasText(userAccount)){
            return;
        }
        channelManager.unRegister(userAccount);
    }
}
