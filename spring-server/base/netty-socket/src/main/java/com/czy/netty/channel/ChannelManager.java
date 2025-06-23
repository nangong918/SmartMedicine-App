package com.czy.netty.channel;


import com.czy.api.domain.entity.model.ResponseBodyProto;
import com.czy.netty.constant.ChannelAttr;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

/**
 * @author 13225
 * @date 2025/2/12 15:38
 * Channel管理者
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ChannelManager {

    private final ConcurrentMap<Long, Channel> userChannels;
    private final boolean isDebug;

    /**
     * 注册channel
     * @param senderId 发送者的userId
     * @param channel       channel
     */
    public void register(Long senderId, Channel channel) {
        if (channel == null){
            log.error("register::channel is null");
            return;
        }
        if (senderId == null){
            log.warn("senderId is null");
            return;
        }

        channel.attr(ChannelAttr.UID).set(senderId);
        userChannels.put(senderId, channel);

        log.info("Client registered: [senderId:{}]", senderId);
    }

    /**
     * 移除channel
     * @param senderId 发送者userId
     */
    public void unRegister(Long senderId) {
        if (senderId == null){
            log.error("unRegister::senderId is null");
            return;
        }

        Channel channel = userChannels.get(senderId);
        if (channel != null/* && channel.isActive()*/) {
            if (!channel.isActive()){
                log.warn("channel is not active");
            }
            channel.close();
            userChannels.remove(senderId);
            log.info("Client unregistered: [senderId:{}]", senderId);
        }
        else {
            log.warn("unRegister::channel is null");
        }
    }

    /**
     * channel失效
     * @param ctx   channel上下文
     */
    public void channelInactive(ChannelHandlerContext ctx){
        Long userId = ctx.channel().attr(ChannelAttr.UID).get();
        // 移除失效的连接
        if (userId != null) {
            // 移除失效的连接，并删除会话
            userChannels.remove(userId);
        }
        log.info("Client disconnected: {}, UID: {}",
                ctx.channel().remoteAddress(),
                ctx.channel().attr(ChannelAttr.UID).get());
    }

    /**
     * 给客户端推送消息
     * @param receiverId   接收者userId
     * @param responseBody      消息体
     */
    public void pushToClient(Long receiverId, ResponseBodyProto.ResponseBody responseBody) {
        if (receiverId == null){
            log.warn("push::receiverId is null");
            return;
        }
        if (responseBody == null){
            log.warn("push::responseBody is null");
            return;
        }
        if (isDebug){
            log.info("Debug::responseBody: {}", responseBody);
        }
        Channel channel = userChannels.get(receiverId);
        if (channel == null){
            log.warn("给[receiverId:{}]消息推送失败，原因：channel是空", receiverId);
            return;
        }
        if (channel.isActive()) {
            try {
                channel.writeAndFlush(responseBody);
            } catch (Exception e){
                log.error("给[receiverId:{}]消息推送失败", receiverId, e);
            }
        }
        else {
            log.warn("给[receiverId:{}]消息推送失败，原因：channel未激活", receiverId);
        }
    }

    /**
     * 检查用户是否在线
     * @param userId   用户Account
     * @return  用户是否在线
     */
    public boolean checkIsOnline(Long userId) {
        if (userChannels.containsKey(userId)){
            Channel channel = userChannels.get(userId);
            return channel != null && channel.isActive();
        }
        return false;
    }
}
