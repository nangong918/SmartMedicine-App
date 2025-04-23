package com.czy.appcore.network.netty.constant;



import android.text.TextUtils;

import com.czy.appcore.network.netty.annotation.MsgTranslator;
import com.czy.dal.constant.Constants;

import java.lang.reflect.Field;


public class MessageTypeTranslator {

    public static String translate(String reqType){
        if (!TextUtils.isEmpty(reqType)){
            // 遍历所有内部类
            Class<?>[] classes = RequestMessageType.class.getDeclaredClasses();
            for (Class<?> clazz : classes) {
                try {
                    // 获取所有公共字段
                    Field[] fields = clazz.getFields();
                    for (Field field : fields){
                        if (reqType.equals(field.get(null))){
                            MsgTranslator annotation = field.getAnnotation(MsgTranslator.class);
                            // 返回响应类型
                            if (annotation != null){
                                return annotation.responseType();
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            return null; // 字段未找到或没有注解
        }
        return null;
    }


    public static String translateClean(String reqType){
        String resType = translate(reqType);
        // 如果是null，则设置为空字符串
        resType = resType == null ? "" : resType;
        // 如果是http请求，则不设置type
        resType = Constants.MESSAGE_TYPE_HTTP.equals(resType) ? "" : resType;
        return resType;
    }
}
