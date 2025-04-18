package com.czy.netty.handler;





import com.czy.api.domain.entity.model.RequestBodyProto;
import com.czy.netty.channel.ChannelManager;
import com.czy.netty.event.SpringCloudEventManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/2/5 14:51
 */

@Slf4j
@RequiredArgsConstructor // 自动生成构造函数
@Component
// 该处理器的实例可以被多个通道（Channel）共享，而不必为每个通道创建新的实例
@ChannelHandler.Sharable
public class RequestMessageHandler extends SimpleChannelInboundHandler<RequestBodyProto.RequestBody> {
    
    private final ChannelManager channelManager;
    private final SpringCloudEventManager eventManager;


    /**
     * 当接收到消息时调用
     * @param chc   通道
     * @param msg   消息
     * @throws Exception    抛出异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext chc, RequestBodyProto.RequestBody msg) throws Exception {
        eventManager.process(chc.channel(), msg);
    }
    /**
     * 当通道成功连接到远程节点时调用此方法。通常用于初始化与该连接相关的状态
     * @param ctx   通道
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 初始化与连接相关的状态
        String uid = ctx.channel().id().asShortText();
        log.info("User connected: {}", uid);
        // 仅打印连接信息，实际注册在收到消息后处理
        log.info("Channel connected: {}", ctx.channel().remoteAddress());
    }

    /**
     * 当通道从远程节点断开时调用此方法。这通常意味着连接已经关闭
     * @param ctx   通道
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        channelManager.channelInactive(ctx);
    }

    /**
     * 当出现异常时调用此方法
     * @param ctx   通道
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Error occurred: ", cause);
        String uid = ctx.channel().id().asShortText();
        log.info("User disconnected: {}", uid);
        ctx.close();
    }
}
