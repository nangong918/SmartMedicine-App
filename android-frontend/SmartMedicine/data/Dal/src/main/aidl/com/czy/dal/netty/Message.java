package com.czy.dal.netty;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.baseUtilsLib.object.BeanUtil;
import com.czy.dal.constant.Constants;
import com.czy.dal.model.RequestBodyProto;
import com.czy.dal.model.ResponseBodyProto;

import java.util.HashMap;
import java.util.Map;

public class Message implements Parcelable, BaseBean {

    private static final String TAG = Message.class.getSimpleName();

    public String code;
    public String message;
    public String senderId;
    public String receiverId;
    public String type;
    public Map<String, String> data;
    public Long timestamp;

    // 无参构造器
    public Message(){
        this.code = "";
        this.message = "";
        this.senderId = "";
        this.receiverId = "";
        this.type = "";
        this.data = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    // Java 构造器
    // Request Message
    public Message(String senderId, String receiverId, String type, Map<String, String> data, Long timestamp) {
        this.code = "";
        this.message = "";
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }

    // dal 转化构造器
    public Message(Message message){
        this.code = message.code;
        this.message = message.message;
        this.senderId = message.senderId;
        this.receiverId = message.receiverId;
        this.type = message.type;
        this.data = new HashMap<>();
        this.data.putAll(message.data);
        this.timestamp = message.timestamp;
    }

    // Response Message
    public Message(String code, String message, String senderId, String receiverId, String type, Map<String, String> data, Long timestamp) {
        this.code = code;
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }

    // Parcelable 构造函数
//    protected Message(Parcel in) {
//        code = in.readString();
//        message = in.readString();
//        senderId = in.readString();
//        receiverId = in.readString();
//        type = in.readString();
//        data = new HashMap<>();
//        in.readMap(data, String.class.getClassLoader());
//        timestamp = in.readLong();
//    }

    protected Message(Parcel in){
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in){
        Log.i("NettySocketService", "readFromParcel::");
        code = in.readString();
        message = in.readString();
        senderId = in.readString();
        receiverId = in.readString();
        type = in.readString();

        // 反序列化Map
        int size = in.readInt();
        if (size >= 0) {
            data = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                String key = in.readString();
                String value = in.readString();
                data.put(key, value);
            }
        }
        else {
            data = null;
        }

        timestamp = in.readLong();
//        in.readMap(data, String.class.getClassLoader());
    }

    public static final Creator<Message> CREATOR = new Creator<>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(code);
//        dest.writeString(message);
//        dest.writeString(senderId);
//        dest.writeString(receiverId);
//        dest.writeString(type);
//        dest.writeMap(data);
//        dest.writeLong(timestamp);
//    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.i("NettySocketService", "readFromParcel::");
        // 基础字段
        dest.writeString(code);
        dest.writeString(message);
        dest.writeString(senderId);
        dest.writeString(receiverId);
        dest.writeString(type);

        // 序列化Map（需要处理null）
        if (data == null) {
            dest.writeInt(-1);
        }
        else {
            dest.writeInt(data.size());
            for (Map.Entry<String, String> entry : data.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue() != null ? entry.getValue() : "");
            }
        }

        dest.writeLong(timestamp != null ? timestamp : 0L);
    }

    // 从 RequestBody 转换为 Message
    public static Message fromRequestBody(RequestBodyProto.RequestBody requestBody) {
        Message message = new Message();
        message.senderId = requestBody.getSenderId();
        message.receiverId = requestBody.getReceiverId();
        message.type = requestBody.getType();
        message.data = new HashMap<>(requestBody.getDataMap());
        message.timestamp = requestBody.getTimestamp();
        return message;
    }

    // 从 ResponseBody 转换为 Message
    public static Message fromResponseBody(ResponseBodyProto.ResponseBody responseBody) {
        Message message = new Message();
        message.code = responseBody.getCode();
        message.message = responseBody.getMessage();
        message.senderId = responseBody.getSenderId();
        message.receiverId = responseBody.getReceiverId();
        message.type = responseBody.getType();
        message.data = new HashMap<>(responseBody.getDataMap());
        message.timestamp = responseBody.getTimestamp();
        return message;
    }

    // 将 Message 转换为 RequestBody
    public RequestBodyProto.RequestBody toRequestBody() {
        return RequestBodyProto.RequestBody.newBuilder()
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setType(type)
                .putAllData(data)
                .setTimestamp(timestamp)
                .build();
    }

    // 将 Message 转换为 ResponseBody
    public ResponseBodyProto.ResponseBody toResponseBody() {
        return ResponseBodyProto.ResponseBody.newBuilder()
                .setCode(code)
                .setMessage(message)
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setType(type)
                .putAllData(data)
                .setTimestamp(timestamp)
                .build();
    }

    // object(request) -> message
    public static Message getRequestBody(Object request, String senderId, String receiverId, String type){
        Map<String, String> dataMap = new HashMap<>();
        try {
            dataMap = BeanUtil.beanToStrMap(request);
        } catch (Exception ignored) {
        }
        Message message = new Message();
        message.data = dataMap;
        message.senderId = senderId;
        message.receiverId = receiverId == null ? Constants.SERVER_ID : receiverId;
        message.type = type;
        try {
            message.timestamp = Long.valueOf(dataMap.get("timestamp"));
        } catch (Exception e){
            Log.d(TAG, "getRequestBody::timestamp: " + e.getMessage());
            message.timestamp = System.currentTimeMillis();
        }
        return message;
    }

    // object(response) -> message
    public static Message getResponseBody(Object response, String senderId, String receiverId, String type){
        Map<String, String> dataMap = new HashMap<>();
        try {
            dataMap = BeanUtil.beanToStrMap(response);
        } catch (Exception ignored) {
        }
        Message message = new Message();
        message.data = dataMap;
        message.senderId = senderId == null ? Constants.SERVER_ID : senderId;
        message.receiverId = receiverId;
        message.type = type;
        try {
            message.timestamp = Long.valueOf(dataMap.get("timestamp"));
        } catch (Exception e){
            Log.d(TAG, "getResponseBody::timestamp: " + e.getMessage());
            message.timestamp = System.currentTimeMillis();
        }
        return message;
    }
}
