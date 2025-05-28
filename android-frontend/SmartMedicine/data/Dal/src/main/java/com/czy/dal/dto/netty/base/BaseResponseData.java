package com.czy.dal.dto.netty.base;


import android.util.Log;


import com.czy.baseUtilsLib.object.BeanUtil;
import com.czy.dal.model.ResponseBodyProto;
import com.czy.dal.netty.Message;

import org.jetbrains.annotations.NotNull;

import java.util.Map;


/**
 * @author 13225
 * @date 2025/2/11 23:33
 */

public class BaseResponseData extends BaseRequestData {
    private static final String TAG = BaseResponseData.class.getSimpleName();

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBaseResponseData(String code, String message,
                                    String senderId, String receiverId, String type, String timestamp){
        this.code = code;
        this.message = message;
        this.setSenderId(senderId);
        this.setReceiverId(receiverId);
        this.setType(type);
        this.setTimestamp(timestamp);
    }

    // T -> ResponseBodyProto.ResponseBody
    public static ResponseBodyProto.ResponseBody getResponseBody(@NotNull BaseResponseData baseResponseData) {
        Map<String, String> dataMapStr = getDataMap(baseResponseData);
        return ResponseBodyProto.ResponseBody.newBuilder()
                .setSenderId(baseResponseData.getSenderId())
                .setReceiverId(baseResponseData.getReceiverId())
                .setType(baseResponseData.getType())
                .setTimestamp(Long.parseLong(baseResponseData.getTimestamp()))
                .putAllData(dataMapStr)
                .setCode(baseResponseData.getCode())
                .setMessage(baseResponseData.getMessage())
                .build();
    }

    // ResponseBodyProto.ResponseBody -> T
    public static <T extends BaseResponseData> T getTargetClass(@NotNull ResponseBodyProto.ResponseBody response, @NotNull Class<T> tClazz) {
        T t = null;
        try {
            t = tClazz.getDeclaredConstructor().newInstance();
            Map<String, String> dataMap = response.getDataMap();
            BeanUtil.copyPropertiesFromMap(dataMap, t);
            t.setSenderId(response.getSenderId());
            t.setReceiverId(response.getReceiverId());
            t.setType(response.getType());
            t.setTimestamp(String.valueOf(response.getTimestamp()));
            t.setCode(response.getCode());
            t.setMessage(response.getMessage());
            return t;
        } catch (Exception e) {
            Log.e(TAG,"数据转换异常, class: "+ tClazz.getName() +
                    ", dataMap: " + response.getDataMap(), e);
            return null;
        }
    }

    // Message -> T
    public static <T extends BaseResponseData> T getResponseFromMessage(@NotNull Message message, @NotNull Class<T> tClazz) {
        T t = null;
        try {
            t = tClazz.getDeclaredConstructor().newInstance();
            Map<String, String> dataMap = message.data;
            BeanUtil.copyPropertiesFromMap(dataMap, t);
            t.setSenderId(message.senderId);
            t.setReceiverId(message.receiverId);
            t.setType(message.type);
            t.setTimestamp(String.valueOf(message.timestamp));
            t.setCode(message.code);
            t.setMessage(message.message);
            return t;
        } catch (Exception e) {
            Log.e(TAG,"数据转换异常, class: "+ tClazz.getName() +
                    ", dataMap: " + message.data, e);
            return null;
        }
    }

    // T -> Message
    public static Message getMessage(@NotNull BaseResponseData baseResponseData) {
        Map<String, String> dataMapStr = getDataMap(baseResponseData);
        return new Message(
                baseResponseData.getCode(),
                baseResponseData.getMessage(),
                baseResponseData.getSenderId(),
                baseResponseData.getReceiverId(),
                baseResponseData.getType(),
                dataMapStr,
                Long.parseLong(baseResponseData.getTimestamp())
        );
    }
}
