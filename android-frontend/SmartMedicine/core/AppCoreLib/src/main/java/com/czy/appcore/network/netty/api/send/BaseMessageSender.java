package com.czy.appcore.network.netty.api.send;

import android.util.Log;


import com.czy.appcore.network.netty.annotation.MessageType;
import com.czy.dal.netty.Message;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BaseMessageSender<T> {

    public static final String TAG = "MessageSender";

    // 获取当前实现类的 Class 对象
    @SuppressWarnings("unchecked")
    public Class<T> getCurrentClass() {
        // 获取当前类的泛型类型
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType parameterizedType) {
            // 获取泛型的实际类型
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }
        throw new IllegalArgumentException("No generic parameter found");
    }

    // 获取当前实现类的所有方法
    public Method[] getAllMethods() {
        try {
            Class<T> clazz = getCurrentClass();
            return clazz.getDeclaredMethods();
        } catch (Exception e){
            Log.e(TAG, "setUrlPathFromAnnotation::clazz: ", e);
            return null;
        }
    }

    // 通过方法名获取对应的 Method 对象
    private Method getMethodByName(String methodName) {
        for (Method method : getAllMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    // 从注释中获取请求类型
    protected void setMessageTypeFromAnnotation(Message requestBody, String methodName) {
        try {
            Method method = getMethodByName(methodName);
            if (method != null){
                MessageType messageType = method.getAnnotation(MessageType.class);
                if (messageType != null) {
                    requestBody.type = messageType.value(); // 设置请求体的 urlPath
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setUrlPathFromAnnotation::methodName: " + methodName, e);
        }
    }

    // 从注释中获取请求类型
    protected String getDescFromAnnotation(String methodName) {
        try {
            Method method = getMethodByName(methodName);
            if (method != null){
                MessageType messageType = method.getAnnotation(MessageType.class);
                if (messageType != null) {
                    return messageType.value() + " \n " + messageType.desc(); // 设置请求体的 urlPath
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setUrlPathFromAnnotation::methodName: " + methodName, e);
        }
        return"";
    }
}
