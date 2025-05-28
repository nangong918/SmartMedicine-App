// IMessageListener.aidl
package com.czy.appcore.netty;

// Declare any non-default types here with import statements

import com.czy.dal.netty.Message;


/**
 * 1.管理NettyManager的生命周期（保活）；分离Netty的WebSocket网络请求，避免主线程阻塞；
 * 2.进行Socket消息接收与发送：对外提供接SendMessage接口和ReceiveMessage监听
 * 3.管理网络状态：对外提供connect，disconnect，reconnect接口和NetworkStateListener监听
 * 4.管理通知栏：对外提供showNotification，hideNotification
 */
interface IMessageListener {
    // 接收服务端推送的消息
    void onMessageReceived(in Message message);

    // 网络状态变化
    void onConnectionStatusChanged(in String netWorkState);
}