// IMessageService.aidl
package com.czy.appcore.netty;

// Declare any non-default types here with import statements

import com.czy.dal.netty.Message;
import com.czy.appcore.netty.IMessageListener;


/**
 * 1.管理NettyManager的生命周期（保活）；分离Netty的WebSocket网络请求，避免主线程阻塞；
 * 2.进行Socket消息接收与发送：对外提供接SendMessage接口和ReceiveMessage监听
 * 3.管理网络状态：对外提供connect，disconnect，reconnect接口和NetworkStateListener监听
 * 4.管理通知栏：对外提供showNotification，hideNotification
 */
interface IMessageService {
    // 连接Socket
    void connect(in String uid);

    // 断开连接
    void disconnect();

    // 从新连接
    void reconnect(in String uid);

    // 发送消息到服务端
    void sendMessage(in Message message);

    // 接收服务端推送的消息
//    void onMessageReceived(in Message message);

    // 注册消息回调监听
    void registerListener(in IMessageListener listener);

    // 取消注册监听
    void unregisterListener(in IMessageListener listener);

    // 显示通知栏：String标题，String内同，int Icon ResId
    void showNotification(in String title, in String content, in int iconResId);

    // 隐藏通知栏
    void hideNotification();
}