package com.czy.netty.service;



import com.czy.api.domain.entity.model.RequestBodyProto;
import com.czy.springUtils.util.IpUtil;
import com.czy.netty.handler.RequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadFactory;

/**
 * @author 13225
 * @date 2025/2/5 13:58
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class NettyServer implements DisposableBean {

    /**
     * 负责接受客户端的连接请求。
     * 通常只有少量线程，足以处理连接请求即可。
     * 处理 I/O 操作的开销相对较小，因为它的任务主要是监听端口并接受连接。
     */
    private EventLoopGroup bossGroup;
    /**
     * 负责处理已连接客户端的 I/O 操作（读/写消息）。
     * 通常需要更多的线程，因为每个客户端连接都可能需要一个线程来处理其 I/O 操作。
     * 处理 I/O 操作的开销比较大，因此需要更多的资源来处理并发连接。
     */
    private EventLoopGroup workerGroup;

    // 端口
    private final Integer nettyPort;
    // 消息处理器
    private final RequestMessageHandler messageHandler;

    @PostConstruct
    public void start() {
        initEventLoopGroup();

        ServerBootstrap bootstrap = createServerBootstrap(this.bossGroup, this.workerGroup);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline chp = socketChannel.pipeline();
//                // 它将 HTTP 请求解码为 HttpRequest 对象
//                chp.addLast(new HttpServerCodec());
//                // 支持 HTTP 响应的分块传输
//                chp.addLast(new ChunkedWriteHandler());
//                // 它可以限制聚合的消息大小（这里设置为 4KB）
//                chp.addLast(new HttpObjectAggregator(4 * 1024));
                // 处理粘包/拆包
                chp.addLast(new LengthFieldBasedFrameDecoder(
                        1024 * 1024,
                        0, 4,
                        0, 4));
                chp.addLast(new LengthFieldPrepender(4));
                // Protobuf编解码
                chp.addLast(new ProtobufDecoder(RequestBodyProto.RequestBody.getDefaultInstance()));
                chp.addLast(new ProtobufEncoder());
                // 业务处理器
                chp.addLast(messageHandler);
            }
        });

        try {
            // 绑定端口，并启动服务器
            ChannelFuture future = bootstrap.bind(nettyPort).syncUninterruptibly();
            future.channel().closeFuture().addListener(f -> destroy());
            log.info(getLogBanner(IpUtil.getLocalIp(), nettyPort));
        } catch (Exception e){
            log.error("Netty Server start error", e);
        }
    }

    private static String getLogBanner(String ip, int port) {
        String url = "Netty Socket Server Url: [ws://" + ip + ":" + port + "]";
        String border = "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *";
        int totalLength = border.length(); // 获取边框的总长度
        // 计算前后空格
        int padding = (totalLength - url.length() - 2) / 2; // 计算左右空格数，减去前后的星号和空格的总长度
        StringBuilder sb = new StringBuilder();
        String spaces = new String(new char[totalLength - 2]).replace('\0', ' ');
        String spaces2 = new String(new char[padding]).replace('\0', ' ');
        // 添加边框和 URL
        sb.append("\n\n")
                .append(border).append("\n")
                .append("*").append(spaces).append("*\n")
                .append("*").append(spaces).append("*\n")
                .append("*").append(spaces2).append(url).append(spaces2).append("*\n")
                .append("*").append(spaces).append("*\n")
                .append("*").append(spaces).append("*\n")
                .append(border).append("\n");

        return sb.toString();
    }

    /**
     * 初始化 EventLoopGroup
     */
    private void initEventLoopGroup() {
        ThreadFactory bossThreadFactory = r -> {
            Thread thread = new Thread(r);
            thread.setName("nio-boss-" + thread.getId());
            return thread;
        };

        ThreadFactory workerThreadFactory = r -> {
            Thread thread = new Thread(r);
            thread.setName("nio-worker-" + thread.getId());
            return thread;
        };

        if (isLinuxSystem()){
            bossGroup = new EpollEventLoopGroup(bossThreadFactory);
            workerGroup = new EpollEventLoopGroup(workerThreadFactory);
            log.info("系统是Linux系统，使用epoll");
        }else {
            bossGroup = new NioEventLoopGroup(bossThreadFactory);
            workerGroup = new NioEventLoopGroup(workerThreadFactory);
            log.info("系统是Windows系统，使用nio");
        }
    }
    private boolean isLinuxSystem(){
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux");
    }

    /**
     * 创建ServerBootstrap;Netty 中，ServerBootstrap 是用于配置和启动服务器的主要类
     *  1.配置了group
     *  2.配置了channel
     *  3.配置了childOption
     * @return  ServerBootstrap
     */
    private ServerBootstrap createServerBootstrap(EventLoopGroup bossGroup, EventLoopGroup workerGroup){
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 设置 EventLoopGroup
        bootstrap.group(bossGroup, workerGroup);
        // 设置子通道选项
        // ChannelOption.TCP_NODELAY：设置为 true 以禁用 Nagle 算法。这有助于减少延迟，适合需要实时响应的应用
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        // ChannelOption.SO_KEEPALIVE：设置为 true 以启用 TCP 的保活功能，这样可以保持连接的活跃性，避免因长时间不活动而断开连接
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        // 如果是 Linux 系统，使用 EpollServerSocketChannel（高性能的 I/O 模型）
        // 否则，使用 NioServerSocketChannel（Java NIO 的标准实现）
        bootstrap.channel(isLinuxSystem() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        // 设置 SO_BACKLOG 选项，用于设置服务器的队列长度，当连接数达到最大时，新的连接将会被拒绝。
//        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        return bootstrap;
    }

    @Override
    public void destroy() throws Exception {
        destroyEventLoopGroup();
    }

    /**
     * 执行注销SOCKET服务
     */
    private void destroyEventLoopGroup() {
        if(bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown() ) {
            try {
                bossGroup.shutdownGracefully();
            } catch(Exception ignore) {
            }
        }

        if(workerGroup != null && !workerGroup.isShuttingDown() && !workerGroup.isShutdown() ) {
            try {
                workerGroup.shutdownGracefully();
            } catch(Exception ignore) {
            }
        }
    }
}
