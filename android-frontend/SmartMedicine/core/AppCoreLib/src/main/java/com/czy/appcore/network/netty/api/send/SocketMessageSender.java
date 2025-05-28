package com.czy.appcore.network.netty.api.send;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.czy.appcore.netty.IMessageService;
import com.czy.dal.constant.Constants;
import com.czy.baseUtilsLib.object.BeanUtil;
import com.czy.dal.dto.netty.forwardMessage.SendImageRequest;
import com.czy.dal.dto.netty.request.AddUserRequest;
import com.czy.dal.dto.netty.request.HandleAddedUserRequest;
import com.czy.dal.dto.netty.request.HaveReadMessageRequest;
import com.czy.dal.dto.netty.request.PostCollectRequest;
import com.czy.dal.dto.netty.request.PostCommentRequest;
import com.czy.dal.dto.netty.request.PostDisLikeRequest;
import com.czy.dal.dto.netty.request.PostFolderRequest;
import com.czy.dal.dto.netty.request.PostForwardRequest;
import com.czy.dal.dto.netty.request.PostLikeRequest;
import com.czy.dal.dto.netty.request.UserBrowseTimeRequest;
import com.czy.dal.dto.netty.request.UserClickPostRequest;
import com.czy.dal.netty.Message;
import com.czy.dal.dto.netty.request.DisconnectRequest;
import com.czy.dal.dto.netty.request.RegisterRequest;
import com.czy.dal.dto.netty.forwardMessage.SendTextDataRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 重构：将具体的发送Request体对外提供，
 * 内部将对外的Request体转为Message交给AIDL
 * AIDL将消息交给channel.writeAndFlush
 */
public class SocketMessageSender extends BaseMessageSender<SocketSendApi> implements SocketSendApi{

    private static final String TAG = SocketMessageSender.class.getSimpleName();

    private final IMessageService messageService;
//    private final Channel channel;
    private String senderId;

    public SocketMessageSender(@NonNull String senderId, @NonNull IMessageService messageService) {
//        this.channel = channel;
        this.senderId = senderId;
        this.messageService = messageService;
        Log.d("Socket", "init::SocketMessageSender: " + senderId);
    }

    private void resetSenderId(String senderId){
        this.senderId = senderId;
    }

    private Message getRequestBody(Object request, String receiverId){
        Map<String, String> dataMap = new HashMap<>();
        try {
            dataMap = BeanUtil.beanToStrMap(request);
        } catch (Exception ignored) {
        }
        Message message = new Message();
        message.data = dataMap;
        message.senderId = this.senderId;
        message.receiverId = receiverId == null ? Constants.SERVER_ID : receiverId;
        // type 在 setMessageTypeFromAnnotation设置
        try {
            message.timestamp = Long.valueOf(dataMap.get("timestamp"));
        } catch (Exception e){
            Log.d(TAG, "getRequestBody::timestamp: " + e.getMessage());
            message.timestamp = System.currentTimeMillis();
        }
        return message;
    }

    private void sendRequest(Message message){
//        RequestBodyProto.RequestBody requestBodyProto = message.toRequestBody();
//        sendMessage(requestBodyProto);
        Log.d(TAG, "sendMessage:1:senderId: " + message.senderId);
        Log.d(TAG, "sendMessage:2:receiverId: " + message.receiverId);
        Log.d(TAG, "sendMessage:3:type: " + message.type);
        Log.d(TAG, "sendMessage:4:DataMap: " + JSON.toJSONString(message.data));
        Log.d(TAG, "sendMessage:5:timestamp: " + message.timestamp);

        try {
            messageService.sendMessage(message);
        } catch (RemoteException e) {
            Log.e(TAG, "messageService.sendMessage To RemoteService Error: ", e);
        } catch (Exception e) {
            Log.e(TAG, "sendMessage Error: ", e);
        }
    }

    private void sendRequest(Object request, String receiverId, String methodName){
        Message message = getRequestBody(request, receiverId);
        super.setMessageTypeFromAnnotation(message, methodName);
        sendRequest(message);
    }

    @Override
    public void connect(@NonNull RegisterRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void disconnect(@NonNull DisconnectRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void sendTextToUser(SendTextDataRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, request.getReceiverId(), methodName);
    }

    @Override
    public void sendTextToGroup(SendTextDataRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, request.getReceiverId(), methodName);
    }

    @Override
    public void addFriend(AddUserRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, request.getReceiverId(), methodName);
    }

    @Override
    public void handleAddedUser(HandleAddedUserRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, request.getReceiverId(), methodName);
    }

    @Override
    public void readMessage(HaveReadMessageRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, request.getReceiverId(), methodName);
    }

    @Override
    public void sendImageToUser(SendImageRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, request.getReceiverId(), methodName);
    }

    @Override
    public void likePost(PostLikeRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void collectPost(PostCollectRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void commentPost(PostCommentRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void forwardPost(PostForwardRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void collectFolder(PostFolderRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void notInterested(PostDisLikeRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void uploadClickEvent(UserClickPostRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

    @Override
    public void uploadBrowseEvent(UserBrowseTimeRequest request) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        sendRequest(request, Constants.SERVER_ID, methodName);
    }

//    private void sendMessage(RequestBodyProto.RequestBody message){
//        if (channel.isActive()){
//            Log.d(TAG, "sendMessage:1:senderId: " + message.getSenderId());
//            Log.d(TAG, "sendMessage:2:receiverId: " + message.getReceiverId());
//            Log.d(TAG, "sendMessage:3:type: " + message.getType());
//            Log.d(TAG, "sendMessage:4:DataMap: " + JSON.toJSONString(message.getDataMap()));
//            Log.d(TAG, "sendMessage:5:timestamp: " + message.getTimestamp());
//
//            // 发送消息
//            channel.writeAndFlush(message);
////            messageService.sendMessage(message);
//        }
//        else {
//            Log.d(TAG, "sendMessage: channel is not active");
//        }
//    }
}
