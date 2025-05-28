package com.czy.dal.dto.netty.base;

import android.util.Log;


import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.baseUtilsLib.object.BeanUtil;
import com.czy.dal.model.RequestBodyProto;
import com.czy.dal.netty.Message;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * @author 13225
 * @date 2025/2/8 19:04
 */

public class BaseRequestData implements BaseBean {

    private static final String TAG = BaseRequestData.class.getSimpleName();

    public String senderId;
    public String receiverId;
    public String type;
    public String timestamp = String.valueOf(System.currentTimeMillis());

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // T -> ResponseBodyProto.ResponseBody
    public static RequestBodyProto.RequestBody getRequestBody(@NotNull BaseRequestData baseRequestData) {
        Map<String, String> dataMapStr = getDataMap(baseRequestData);
        return RequestBodyProto.RequestBody.newBuilder()
                .setSenderId(baseRequestData.getSenderId())
                .setReceiverId(baseRequestData.getReceiverId())
                .setType(baseRequestData.getType())
                .setTimestamp(Long.parseLong(baseRequestData.getTimestamp()))
                .putAllData(dataMapStr)
                .build();
    }

    // ResponseBodyProto.ResponseBody -> T
    public static <T extends BaseRequestData> T getTargetClass(@NotNull RequestBodyProto.RequestBody requestBody, @NotNull Class<T> tClazz) {
        T t = null;
        try {
            t = tClazz.getDeclaredConstructor().newInstance();
            Map<String, String> dataMap = requestBody.getDataMap();
            BeanUtil.copyPropertiesFromMap(dataMap, t);
            t.setSenderId(requestBody.getSenderId());
            t.setReceiverId(requestBody.getReceiverId());
            t.setType(requestBody.getType());
            t.setTimestamp(String.valueOf(requestBody.getTimestamp()));
            return t;
        } catch (Exception e) {
            Log.e(TAG,"数据转换异常, class: "+ tClazz.getName() +
                    ", dataMap: " + requestBody.getDataMap(), e);
            return null;
        }
    }
    protected static Map<String, String> getDataMap(@NotNull BaseRequestData baseRequestData) {
        Map<String, String> dataMapStr = new HashMap<>();
        try {
            // 可能需要检查子类的私有字段是否转入
            Map<String, Object> dataMap = BeanUtil.beanToMap(baseRequestData);
            dataMapStr = dataMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue() != null ? entry.getValue().toString() : ""
                    ));
        } catch (Exception ignored){
        }
        return dataMapStr;
    }

    // Message -> T
    public static <T extends BaseRequestData> T getRequestFromMessage(@NotNull Message message, @NotNull Class<T> tClazz) {
        T t = null;
        try {
            t = tClazz.getDeclaredConstructor().newInstance();
            Map<String, String> dataMap = message.data;
            BeanUtil.copyPropertiesFromMap(dataMap, t);
            t.setSenderId(message.senderId);
            t.setReceiverId(message.receiverId);
            t.setType(message.type);
            t.setTimestamp(String.valueOf(message.timestamp));
            return t;
        } catch (Exception e) {
            Log.e(TAG,"数据转换异常, class: "+ tClazz.getName() +
                    ", dataMap: " + message.data, e);
            return null;
        }
    }

    // T -> Message
    public static Message getMessage(@NotNull BaseRequestData baseRequestData) {
        Map<String, String> dataMapStr = getDataMap(baseRequestData);
        return new Message(
                baseRequestData.getSenderId(),
                baseRequestData.getReceiverId(),
                baseRequestData.getType(),
                dataMapStr,
                Long.parseLong(baseRequestData.getTimestamp())
        );
    }
}
