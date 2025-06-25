package com.czy.api.api.socket;

/**
 * @author 13225
 * @date 2025/3/30 0:03
 */
public interface ChannelService {

    // 查询是否在线
    boolean isOnline(Long userId);

    // 强制下线请求
    void forceOffline(Long userId);
}
