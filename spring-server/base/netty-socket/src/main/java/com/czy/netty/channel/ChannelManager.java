package com.czy.netty.channel;



import com.czy.netty.constant.ChannelAttr;
import com.czy.api.domain.entity.model.ResponseBodyProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    private final ConcurrentMap<String, Channel> userChannels;
    private final boolean isDebug;

    /**
     * 注册channel
     * @param senderAccount 发送者Account
     * @param channel       channel
     */
    public void register(String senderAccount, Channel channel) {
        if (channel == null){
            log.error("register::channel is null");
            return;
        }
        if (senderAccount == null){
            log.warn("senderAccount is null");
            return;
        }

        channel.attr(ChannelAttr.UID).set(senderAccount);
        userChannels.put(senderAccount, channel);

        log.info("Client registered: [senderAccount:{}]", senderAccount);
    }

    /**
     * 移除channel
     * @param senderAccount 发送者Account
     */
    public void unRegister(String senderAccount) {
        if (!StringUtils.hasText(senderAccount)){
            log.error("unRegister::senderAccount is null");
            return;
        }

        Channel channel = userChannels.get(senderAccount);
        if (channel != null/* && channel.isActive()*/) {
            channel.close();
            userChannels.remove(senderAccount);
            log.info("Client unregistered: [senderAccount:{}]", senderAccount);
        }
        else {
            log.info("unRegister::channel is null or channel is not active");
        }
    }

    /**
     * channel失效
     * @param ctx   channel上下文
     */
    public void channelInactive(ChannelHandlerContext ctx){
        String userAccount = ctx.channel().attr(ChannelAttr.UID).get();
        // 移除失效的连接
        if (userAccount != null) {
            // 移除失效的连接，并删除会话
            userChannels.remove(userAccount);
        }
        log.info("Client disconnected: {}, UID: {}",
                ctx.channel().remoteAddress(),
                ctx.channel().attr(ChannelAttr.UID).get());
    }

    /**
     * 给客户端推送消息
     * @param receiverAccount   接收者Account
     * @param responseBody      消息体
     */
    public void pushToClient(String receiverAccount, ResponseBodyProto.ResponseBody responseBody) {
        if (!StringUtils.hasText(receiverAccount)){
            log.warn("push::receiverAccount is null");
            return;
        }
        if (responseBody == null){
            log.warn("push::responseBody is null");
            return;
        }
        if (isDebug){
            log.info("Debug::responseBody: {}", responseBody);
        }
        Channel channel = userChannels.get(receiverAccount);
        if (channel != null && channel.isActive()) {
            try {
                channel.writeAndFlush(responseBody);
            } catch (Exception e){
                log.error("给[receiverAccount:{}]消息推送失败", receiverAccount, e);
            }
        }
        else {
            log.warn("给[receiverAccount:{}]消息推送失败，原因：channel是空或者channel未激活", receiverAccount);
        }
    }

    /**
     * 检查用户是否在线
     * @param userAccount   用户Account
     * @return  用户是否在线
     */
    public boolean checkIsOnline(String userAccount) {
        if (userChannels.containsKey(userAccount)){
            Channel channel = userChannels.get(userAccount);
            return channel != null && channel.isActive();
        }
        return false;
    }
}
