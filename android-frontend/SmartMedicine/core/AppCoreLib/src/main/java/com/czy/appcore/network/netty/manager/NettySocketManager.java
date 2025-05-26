package com.czy.appcore.network.netty.manager;

import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.BuildConfig;


import com.czy.appcore.BaseConfig;
import com.czy.appcore.network.netty.connect.ConnectNettyCallback;
import com.czy.appcore.network.netty.connect.NettyConnectChangeCallback;
import com.czy.dal.constant.Constants;
import com.czy.dal.constant.netty.RequestMessageType;
import com.czy.appcore.network.netty.event.OnReceiveMessage;
import com.czy.appcore.network.netty.handler.ResponseBodyHandler;
import com.czy.dal.model.RequestBodyProto;
import com.czy.dal.model.ResponseBodyProto;
import com.czy.dal.netty.Message;
import com.czy.dal.dto.netty.request.RegisterRequest;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * NettySocket连接Manager
 */
public class NettySocketManager {

    public static final String TAG = NettySocketManager.class.getSimpleName();
    // 创建一个事件循环组
    private EventLoopGroup group;
    // 创建一个长连接通道
    private Channel channel;
    private ConnectNettyCallback connectNettyCallback;
    private String host;
    private int port;
    private String userId;
    private final ResponseBodyHandler responseBodyHandler;
    private ScheduledExecutorService executorService;
    private final NettyConnectChangeCallback connectChangeCallback;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);

    public Channel getChannel() {
        return channel;
    }

    public NettySocketManager(
            String host,
            int port,
            @NonNull ConnectNettyCallback callback,
            @NonNull OnReceiveMessage onReceiveMessage,
            @NonNull NettyConnectChangeCallback connectChangeCallback){
        // 创建一个单线程池
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        init(host, port, callback);
        this.responseBodyHandler = new ResponseBodyHandler(onReceiveMessage);
        this.connectChangeCallback = connectChangeCallback;
    }

    public void init(String host, int port, @NonNull ConnectNettyCallback callback){
        this.connectNettyCallback = callback;
        if (TextUtils.isEmpty(host)){
            Log.w(TAG, "init::host is empty");
            callback.onConnectFailure("Host is empty");
            return;
        }
        this.host = host;
        if (port <= 0){
            Log.w(TAG, "init::port is invalid");
            callback.onConnectFailure("Port is invalid");
            return;
        }
        this.port = port;
//        if (TextUtils.isEmpty(userId)){
//            Log.w(TAG, "init::userId is empty");
//            callback.onConnectFailure("User ID is empty");
//            return;
//        }
    }

    public void setUserId(String userId){
        if (TextUtils.isEmpty(userId)){
            Log.w(TAG, "init::userId is empty");
            this.connectNettyCallback.onConnectFailure("User ID is empty");
            return;
        }
        this.userId = userId;
        Log.i(TAG, "setUserId::userId = " + userId);
    }

    public void connect(){
        if (executorService.isShutdown()){
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.execute(() -> {
            group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                // 与ServerBootstrap不同，只需要一个 NioEventLoopGroup
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline pipeline = ch.pipeline();
                                // 添加 HTTP 编解码器
//                                pipeline.addLast(new HttpClientCodec());
//                                pipeline.addLast(new HttpObjectAggregator(4 * 1024));

                                // 处理粘包/拆包
                                pipeline.addLast(new LengthFieldBasedFrameDecoder(
                                        1024 * 1024 * 10, // 10MB最大帧长度
                                        0,
                                        4,
                                        0,
                                        4
                                ));
                                pipeline.addLast(new LengthFieldPrepender(4));
                                // Protobuf编解码
                                pipeline.addLast(new ProtobufDecoder(ResponseBodyProto.ResponseBody.getDefaultInstance()));
                                pipeline.addLast(new ProtobufEncoder());
                                // 业务处理器
                                pipeline.addLast(responseBodyHandler);
                            }
                        });

                Log.i(TAG, "connect::host:" + host + ",port:" + port);
                ChannelFuture future = bootstrap.connect(host, port).sync();
                channel = future.channel();
                Log.i(TAG, "channel == null?:" + (channel == null));

//                initSocketApi();
//                Log.i(TAG, "connect::initSocketApi");

                // 连接成功后发送用户 ID
                sendUserId();

                // 发送心跳连接
                initOnReceiveHeartbeat(this.responseBodyHandler);

                future.channel().closeFuture().sync();

                // 调用连接成功回调
                this.connectNettyCallback.onConnectSuccess();
            } catch (InterruptedException e) {
                Log.e(TAG, "connect", e);
                // 调用连接失败回调
                this.connectNettyCallback.onConnectFailure("Connection interrupted: " + e.getMessage());
            } finally {
                group.shutdownGracefully();
            }
        });
    }

    // 发送消息
    public void sendMessage(RequestBodyProto.RequestBody message) {
        if (channel != null) {
            if (channel.isActive()){
                channel.writeAndFlush(message);
            }
            else {
                Log.w(TAG, "sendMessage::channel is not active");
                // 重新连接
                startReconnect();
            }
        }
        else {
            Log.w(TAG, "sendMessage::channel is null");
        }
    }

    // 发送用户 ID
    private void sendUserId() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAppVersion(BuildConfig.VERSION_NAME);
        registerRequest.setDeviceId(Build.DEVICE);
        registerRequest.setDeviceName(Build.MODEL);
        registerRequest.setLanguage(Locale.getDefault().getLanguage());
        registerRequest.setUuid(UUID.randomUUID().toString());
        registerRequest.setPackageName(BaseConfig.PACKAGE_NAME);
        registerRequest.setOsVersion(Build.VERSION.RELEASE);

        // Object(request) -> message
        Message message = Message.getRequestBody(
                registerRequest,
                this.userId,
                Constants.SERVER_ID,
                RequestMessageType.Connect.CONNECT
        );

        // message -> protobuf
        RequestBodyProto.RequestBody requestBody = message.toRequestBody();

        channel.writeAndFlush(requestBody);
    }

    // 关闭连接
    public void closeConnect() {
        disconnect();
        // 线程池任务取消
        executorService.shutdownNow();
    }

    // 断开连接
    public void disconnect() {
        if (channel != null) {
            channel.close();
            // 清空 channel 引用
            channel = null;
            isConnected.set(false);
            connectChangeCallback.onDisconnect();
        }
    }

    // 重新连接
    public void reconnect(String uid) {
        this.userId = uid;
        // 先断开连接
        disconnect();
        // 重新连接
        connect();
    }

    /**
     * 服务端在连接写空闲60秒的时候发送心跳请求给客户端，所以客户端在空闲120秒后都没有收到任何数据，则关闭链接，并重新创建
     */
    private static final long SEND_PING_DELAY = 60_000;
    private static final long CONNECT_ALIVE_TIME_OUT = 120_000;

    // 设置心跳连接
    public ConnectivityManager.NetworkCallback initHeartBeat(){
        // 连接可靠：发送Pong
        // 发送Pong
        // 连接不可靠：从新连接
        // 标记为未连接
        // 尝试从新连接
        // 心跳连接
        return new ConnectivityManager.NetworkCallback() {
            // 连接可靠：发送Pong
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d(TAG, "onAvailable:" + System.currentTimeMillis());
                setConnected();
                // 发送Ping
                sendPing();
            }

            // 连接不可靠：从新连接
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.e(TAG, "onLost");
                isConnected.set(false); // 标记为未连接
                connectChangeCallback.onDisconnect();
                // 尝试从新连接
                startReconnect();
            }
        };
    }

    // 重连超时
    public static final int RECONNECT_TIME_OUT = 15_000;

    private final AtomicBoolean isReconnecting = new AtomicBoolean(false);

    // 从新连接：每15秒从新连接一次
    private synchronized void startReconnect() {
        if (isReconnecting.compareAndSet(false, true)){
            executorService.submit(() -> {
                while (!isConnected.get()) { // 只要未连接就继续重连
                    reconnect(this.userId); // 尝试重连
                    try {
                        Thread.sleep(RECONNECT_TIME_OUT); // 等待 15 秒
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 恢复中断状态
                        break;
                    }
                }
            });
        }
    }

    private void initOnReceiveHeartbeat(ResponseBodyHandler responseBodyHandler) {
        if (responseBodyHandler == null){
            Log.w(TAG, "心跳连接发送失败，responseBodyHandler是空");
            return;
        }
        // 初始化接收回调
        OnReceiveMessage onReceiveHeartbeat = message -> {
            Message pong = Message.fromResponseBody(message);
            handlePong(pong);
        };
        responseBodyHandler.setReceiveHeartbeat(onReceiveHeartbeat);

        // 初始化Ping线程
        startHeartbeat();
    }

    private void startHeartbeat() {
        executorService.scheduleWithFixedDelay(
                this::sendPing,
                5,
                // 每隔 60 秒发送一次心跳请求
                SEND_PING_DELAY, TimeUnit.MILLISECONDS
        );
        executorService.scheduleWithFixedDelay(
                this::checkConnectionTimeout,
                5,
                // 每120秒检查一次超时
                CONNECT_ALIVE_TIME_OUT, TimeUnit.SECONDS);

        // 设置连接
        setConnected();
    }

    private long lastPongTime = System.currentTimeMillis();

    private void checkConnectionTimeout() {
        if (System.currentTimeMillis() - lastPongTime > CONNECT_ALIVE_TIME_OUT) {
            Log.e(TAG, "Connection timeout, attempting to reconnect...");
            startReconnect();
        }
    }

    private Message getPing() {
        Message ping = new Message();
        ping.type = RequestMessageType.ToServer.PING;
        ping.senderId = this.userId;
        ping.receiverId = Constants.SERVER_ID;
        return ping;
    }

    public void sendPing() {

        if (channel != null){
            Message message = getPing();
            RequestBodyProto.RequestBody requestBody = message.toRequestBody();
            sendMessage(requestBody);
        }
    }

    private void handlePong(Message pong){
        // 刷新重连倒计时
        lastPongTime = pong.timestamp == null ? System.currentTimeMillis() : pong.timestamp;
        // 关闭 startReconnect 线程
        setConnected();
    }

    private synchronized void setConnected(){
        isConnected.set(true);
        isReconnecting.set(false);
        connectChangeCallback.onConnect();
    }
}
