package com.czy.appcore.network.netty.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.netty.IMessageListener;
import com.czy.appcore.netty.IMessageService;
import com.czy.appcore.network.netty.connect.ConnectNettyCallback;
import com.czy.appcore.network.netty.connect.NettyConnectChangeCallback;
import com.czy.appcore.network.netty.event.OnReceiveMessage;
import com.czy.appcore.network.netty.manager.NettySocketManager;
import com.czy.dal.constant.Constants;
import com.czy.dal.model.RequestBodyProto;
import com.czy.dal.netty.Message;

/**
 * RemoteService独立进程管理链接；避免在Application阻塞主线程出现无响应ANR(Android Not Responding)
 * 1.管理NettyManager的生命周期（保活）；分离Netty的WebSocket网络请求，避免主线程阻塞；
 * 2.进行Socket消息接收与发送：对外提供接SendMessage接口和ReceiveMessage监听
 * 3.管理网络状态：对外提供connect，disconnect，reconnect接口和NetworkStateListener监听
 * 4.管理通知栏：对外提供showNotification，hideNotification
 */
public class NettySocketService extends Service {

    public static final String TAG = NettySocketService.class.getSimpleName();

    private NotificationManager notificationManager;

    public NettySocketService() {
        Log.d(TAG, TAG + "::NettySocketService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + "::onCreate");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + "::onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void init(){
        // binder
        binder = new NettySocketServiceBinder();
        // Listener
        initListener();
        // Netty WebSocket Manager
        initNettySocketManager();
    }

    private NettySocketManager nettySocketManager;
    private ConnectNettyCallback connectNettyCallback;
    private OnReceiveMessage onReceiveMessage;
    private IMessageListener messageListener;
    private NettyConnectChangeCallback connectChangeCallback;

    private void initListener() {
        connectNettyCallback = new ConnectNettyCallback() {
            @Override
            public void onConnectSuccess() {
                Log.i(TAG, "onConnectSuccess");
            }

            @Override
            public void onConnectFailure(String errorMessage) {
                Log.e(TAG, "onConnectFailure::" + errorMessage);
            }
        };
        onReceiveMessage = responseBody -> {
            Message message = Message.fromResponseBody(responseBody);
            if (messageListener != null){
                try {
                    Log.i(TAG, "---> SocketResponse: \n" + message.toJsonString());
                    messageListener.onMessageReceived(message);
                } catch (RemoteException e) {
                    Log.e(TAG, "onReceiveMessage::Error ");
                }
            }
            else {
                Log.e(TAG, "onReceiveMessage::messageListener is null");
            }
        };
        connectChangeCallback = new NettyConnectChangeCallback() {
            @Override
            public void onConnect() {
                try {
                    if (messageListener != null){
                        messageListener.onConnectionStatusChanged(Constants.CONNECTED);
                    }
                    else {
                        Log.e(TAG, "onReceiveMessage::messageListener is null");
                    }
                } catch (RemoteException e) {
                    Log.w(TAG, "onConnectionStatusChanged::Error ");
                }
            }

            @Override
            public void onDisconnect() {
                try {
                    if (messageListener != null){
                        messageListener.onConnectionStatusChanged(Constants.DISCONNECTED);
                    }
                    else {
                        Log.e(TAG, "onReceiveMessage::messageListener is null");
                    }
                } catch (RemoteException e) {
                    Log.w(TAG, "onConnectionStatusChanged::Error ");
                }
            }
        };
    }

    private void initNettySocketManager() {

        nettySocketManager = new NettySocketManager(
                BaseConfig.DNS,
                BaseConfig.webSocketPort,
                connectNettyCallback,
                onReceiveMessage,
                connectChangeCallback
        );

        // 注册网络回调
        registerNetworkCallback();
    }

    // 注册网络回调
    private void registerNetworkCallback() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null && nettySocketManager != null) {
            connectivityManager.registerDefaultNetworkCallback(nettySocketManager.initHeartBeat());
        }
    }

    public class NettySocketServiceBinder extends IMessageService.Stub {
        @Override
        public void connect(long uid) throws RemoteException {
            if(Constants.ERROR_ID.equals(uid)){
                Log.w(TAG, "connect::uid is empty");
                return;
            }
            Log.i(TAG, "NettySocketService::sendMessage::uid: " + uid);
            nettySocketManager.setUserId(uid);
            nettySocketManager.connect();
        }

        @Override
        public void disconnect() throws RemoteException {
            nettySocketManager.disconnect();
        }

        @Override
        public void reconnect(long uid) throws RemoteException {
            nettySocketManager.reconnect(uid);
        }

        @Override
        public void sendMessage(Message message) throws RemoteException {
            RequestBodyProto.RequestBody requestBody = message.toRequestBody();
            Log.i(TAG, "NettySocketService::sendMessage::message: " + message.toJsonString());
            nettySocketManager.sendMessage(requestBody);
        }

//        @Override
//        public void onMessageReceived(Message message) throws RemoteException {
//
//        }

        @Override
        public void registerListener(IMessageListener listener) throws RemoteException {
            messageListener = listener;
        }

        @Override
        public void unregisterListener(IMessageListener listener) throws RemoteException {
            if(messageListener == listener){
                messageListener = null;
            }
        }

        @Override
        public void showNotification(String title, String content, int iconResId) throws RemoteException {

        }

        @Override
        public void hideNotification() throws RemoteException {

        }
    }

    private NettySocketServiceBinder binder;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}