package com.czy.appcore.network.netty.api;

import android.text.TextUtils;
import android.util.Log;

import com.czy.appcore.network.netty.annotation.MessageType;
import com.czy.appcore.network.netty.api.receive.ReceiveAddUserApi;
import com.czy.appcore.network.netty.api.receive.ReceiveMessageApi;
import com.czy.appcore.network.netty.queue.SocketMessageQueue;
import com.czy.dal.netty.Message;
import com.czy.dal.dto.netty.base.BaseResponseData;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SocketApiResponseHandler {

    private static final String TAG = SocketApiResponseHandler.class.getSimpleName();

//    private static final Map<String, Method> methodMap = new HashMap<>();
//
//    static {
//        registerMethods(ReceiveMessageApi.class);
//        registerMethods(ReceiveAddUserApi.class);
//    }
//
//    private static void registerMethods(Class<?> apiClass) {
//        for (Method method : apiClass.getDeclaredMethods()) {
//            MessageType messageType = method.getAnnotation(MessageType.class);
//            if (messageType != null) {
//                methodMap.put(messageType.value(), method);
//            }
//        }
//    }

//    public static void handleMessage(Message message, SocketMessageQueue socketMessageQueue){
//        if (message == null || TextUtils.isEmpty(message.type)) {
//            return;
//        }
//
//        // 消息队列缓存
//        Optional.ofNullable(socketMessageQueue).ifPresent(queue -> {
//            // 消息缓存到缓存池
//            queue.receiveMessage(message, message.type);
//        });
//
//        MessageDal messageDal = new MessageDal(
//                message.code,
//                message.message,
//                message.senderId,
//                message.receiverId,
//                message.type,
//                message.data,
//                message.timestamp
//        );
//
//        Method method = methodMap.get(message.type);
//        if (method != null){
//            try {
//                // 获取当前反射方法的第一个参数的类型
//                Class<?> responseClass = method.getParameterTypes()[0];
//
//                Object response = responseClass.getDeclaredMethod("getResponseFromMessage", MessageDal.class, Class.class)
//                        .invoke(null, messageDal, responseClass);
//                if (response != null) {
//                    // EventBus发送消息
//                    EventBus.getDefault().postSticky(
//                            // 强制转换为具体类型并发送到 EventBus
//                            responseClass.cast(response)
//                    );
//                }
//            } catch (Exception e) {
//                Log.e(NettySocketService.TAG, "handleMessage反射消息转化失败", e);
//            }
//        }
//    }

    public static void handleMessage(Message message, SocketMessageQueue socketMessageQueue){
        if (message == null || TextUtils.isEmpty(message.type)){
            return;
        }
        // 缓存池
        Optional.ofNullable(socketMessageQueue)
                        .ifPresent(queue -> {
                            // 消息缓存到缓存池
                            queue.receiveMessage(message, message.type);
                        });

        processMessages(message);

//        if (ResponseMessageType.Chat.RECEIVE_USER_TEXT_MESSAGE.equals(message.type)){
//            UserTextDataResponse response = BaseResponseData.getResponseFromMessage(
//                    messageDal,
//                    UserTextDataResponse.class
//            );
//            if (response != null){
//                // EventBus发送消息
//                EventBus.getDefault().postSticky(response);
//            }
//        }
//        else if (ResponseMessageType.Chat.RECEIVE_GROUP_TEXT_MESSAGE.equals(message.type)){
//            GroupTextDataResponse response = BaseResponseData.getResponseFromMessage(
//                    messageDal,
//                    GroupTextDataResponse.class
//            );
//            if (response != null){
//                // EventBus发送消息
//                EventBus.getDefault().postSticky(response);
//            }
//        }
//        else if (ResponseMessageType.Friend.ADDED_FRIEND.equals(message.type)){
//            AddUserToTargetUserResponse response = BaseResponseData.getResponseFromMessage(
//                    messageDal,
//                    AddUserToTargetUserResponse.class
//            );
//            if (response != null){
//                // EventBus发送消息
//                EventBus.getDefault().postSticky(response);
//            }
//        }
//        else if (ResponseMessageType.Friend.ADD_FRIEND_RESULT.equals(message.type)){
//            HandleAddUserResponse response = BaseResponseData.getResponseFromMessage(
//                    messageDal,
//                    HandleAddUserResponse.class
//            );
//            if (response != null){
//                // EventBus发送消息
//                EventBus.getDefault().postSticky(response);
//            }
//        }
    }
    // 存储 messageType 和对应的处理方法
    private static final Map<String, Class<? extends BaseResponseData>> messageTypeMap;

    static {
        messageTypeMap = new HashMap<>();
        // 处理 ReceiveAddUserApi 接口的注解
        registerApi(ReceiveAddUserApi.class);
        // 处理 ReceiveMessageApi 接口的注解
        registerApi(ReceiveMessageApi.class);
    }

    @SuppressWarnings("unchecked")
    private static void registerApi(Class<?> apiClass) {
        for (Method method : apiClass.getDeclaredMethods()) {
            MessageType messageTypeAnnotation = method.getAnnotation(MessageType.class);
            if (messageTypeAnnotation != null) {
                String messageType = messageTypeAnnotation.value();
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 0) {
                    // 将 messageType 和参数类型映射
                    try {
                        // @SuppressWarnings("unchecked")
                        messageTypeMap.put(messageType, (Class<? extends BaseResponseData>) parameterTypes[0]);
                    } catch (Exception e) {
                        Log.e(TAG, "registerApi反射消息转化失败, messageType: " + messageType, e);
                    }
                }
            }
        }
    }

    public static void processMessages(Message message) {
        Class<? extends BaseResponseData> responseClass = messageTypeMap.get(message.type);
        if (responseClass != null) {
            BaseResponseData response = BaseResponseData.getResponseFromMessage(message, responseClass);
            if (response != null) {
                // 处理响应，例如发送到 EventBus
                EventBus.getDefault().postSticky(response);
                Log.d(TAG, "处理消息成功::class: " + response.getClass());
                Log.d(TAG, "处理消息: " + response.toJsonString());
            }
        }
    }

//    public static void processMessages(Message message) {
//        List<Class<? extends BaseResponseData>> responseClasses = Arrays.asList(
//                HandleAddUserResponse.class,
//                AddUserToTargetUserResponse.class,
//                GroupTextDataResponse.class,
//                UserTextDataResponse.class
//        );
//
//        for (Class<? extends BaseResponseData> responseClass : responseClasses) {
//            BaseResponseData response = BaseResponseData.getResponseFromMessage(message, responseClass);
//            if (response != null) {
//                // 处理响应，例如发送到 EventBus
//                EventBus.getDefault().postSticky(response);
//                Log.d(NettySocketService.TAG, "处理消息成功::class: " + response.getClass());
//            }
//        }
//    }
}
