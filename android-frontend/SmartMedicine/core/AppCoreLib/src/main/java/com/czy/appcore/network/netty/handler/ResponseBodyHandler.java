package com.czy.appcore.network.netty.handler;


import android.util.Log;

import androidx.annotation.NonNull;


import com.czy.dal.constant.netty.ResponseMessageType;
import com.czy.appcore.network.netty.event.OnReceiveMessage;
import com.czy.dal.model.ResponseBodyProto;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ResponseBodyHandler extends SimpleChannelInboundHandler<ResponseBodyProto.ResponseBody> {

    private static final String TAG = "Socket:" + ResponseBodyHandler.class.getSimpleName();

    private final OnReceiveMessage onReceiveMessage;

    public ResponseBodyHandler(@NonNull OnReceiveMessage onReceiveMessage){
        this.onReceiveMessage = onReceiveMessage;
    }

    private OnReceiveMessage onReceiveHeartbeat;
    public void setReceiveHeartbeat(@NonNull OnReceiveMessage onReceiveHeartbeat){
        this.onReceiveHeartbeat = onReceiveHeartbeat;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, @NonNull ResponseBodyProto.ResponseBody msg) throws Exception {
        String type = msg.getType();
        Log.d(TAG, "messageReceived: type: " + type);

        // EventBus将其注册为对应的消息，然后发送给订阅者
        // 来自Server的
        if (type.contains(ResponseMessageType.ToServer.root)){
            if (type.contains(ResponseMessageType.ToServer.PONG) && onReceiveHeartbeat != null){
                onReceiveHeartbeat.onReceiveMessage(msg);
            }
        }
        // 连接消息
        else if (type.contains(ResponseMessageType.Connect.root)){

        }
        // EventBus消息
        else{
            // 将消息转发给 EventListener
            Log.i(TAG, "---> SocketResponse:msg.getReceiverId() \n" + msg.getReceiverId());
            this.onReceiveMessage.onReceiveMessage(msg);
        }
    }

    // Activity存活、未存活：存活，直接接受EventBus消息
    // 未存活：将消息存储在消息队列，Activity存活之后，从消息队列中取出消息，进行消息处理

    // msg -> EventBus -> EventListener -> MainApplication -> Activity
    // EventListener内部处理消息类型：将消息转为
    // Activity:需要接受消息，然后做处理；存在存活与未存活
    // Activity创建之后：从MainApplication检查自己的消息队列
    // 消息队列指针指向消息；消息队列中全部Activity消化消息之后，将接受的消息销毁

    // 消息队列与SQLite持久化

    // 场景情况：1.聊天消息发送到App；ChatList显示消息；ChatActivity在打开对应人页面才能加载消息

    // 慢SQL？SQL读取速度？存储策略？读取70~90条数据怎么保证不检索到70条往后数20条而是直接从70条开始数20条？
}
