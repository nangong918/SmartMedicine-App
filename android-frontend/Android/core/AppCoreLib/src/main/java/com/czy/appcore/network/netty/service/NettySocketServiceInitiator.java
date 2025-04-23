package com.czy.appcore.network.netty.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.czy.appcore.netty.IMessageListener;
import com.czy.appcore.netty.IMessageService;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.dal.constant.Constants;
import com.czy.appcore.network.netty.constant.RequestMessageType;
import com.czy.dal.netty.Message;

public class NettySocketServiceInitiator {

    private IMessageService messageService;
    private boolean isBound;
    private IMessageListener messageListener;
    private SocketMessageSender socketMessageSender;

    public void initRemoteService(Context context, String senderAccount, @NonNull IMessageListener listener) {
        this.messageListener = listener;

        // 发送连接消息
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                messageService = IMessageService.Stub.asInterface(service);
                initSocketMessageSender(senderAccount);
                try {
                    messageService.registerListener(messageListener);
                    isBound = true;
                    Message message = new Message();
                    message.senderId = senderAccount;
                    message.receiverId = Constants.SERVER_ID;
                    message.type = RequestMessageType.Connect.CONNECT;
                    Log.i(NettySocketService.TAG, "new message.senderId: " + message.senderId);
                    // 发送连接消息
                    messageService.connect(senderAccount);
//                    messageService.sendMessage(message);
                } catch (RemoteException e) {
                    Log.e(NettySocketService.TAG, "onServiceConnected: ", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                messageService = null;
                isBound = false;
            }
        };

        Intent intent = new Intent(context, NettySocketService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        Log.i(NettySocketService.TAG, "initRemoteService");
    }

    public void disconnectNetty() {
        if (isBound) {
            try {
                messageService.disconnect();
//                messageService.unregisterListener(messageListener);
//                isBound = false;
                Log.d(NettySocketService.TAG, "disconnectNetty");
            } catch (RemoteException e) {
                Log.e(NettySocketService.TAG, "disconnect: ", e);
            }
        }
    }

    public void disconnectService(){
        if (isBound) {
            try {
                messageService.disconnect();
                messageService.unregisterListener(messageListener);
                isBound = false;
            } catch (RemoteException e) {
                Log.e(NettySocketService.TAG, "disconnect: ", e);
            }
        }
    }

    private void initSocketMessageSender(String uid) {
        socketMessageSender = new SocketMessageSender(
                uid,
                getMessageService()
        );
    }

    // sendMessage
    public SocketMessageSender getMessageSender(){
        if (isConnected()){
            return socketMessageSender;
        }
        else {
            return null;
        }
    }

    public boolean isConnected(){
        return isBound;
    }

    // 获取IMessageService
    public IMessageService getMessageService() {
        return messageService;
    }
}
