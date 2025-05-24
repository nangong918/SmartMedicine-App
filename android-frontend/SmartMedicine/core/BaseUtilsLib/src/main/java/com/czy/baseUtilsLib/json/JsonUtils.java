package com.czy.baseUtilsLib.json;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    protected static final String TAG = JsonUtils.class.getSimpleName();

    /**
     * 反射实现 由类转为JSONObject
     * @param obj   类
     * @return      org.json.JSONObject
     */
    public static org.json.JSONObject toJsonReflect(Object obj) {
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                // 允许访问私有字段
                field.setAccessible(true);
                Object value = field.get(obj);
                jsonObject.put(field.getName(), value);
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "无法访问对象字段: " + e.getMessage(), e);
        } catch (org.json.JSONException e) {
            Log.e(TAG, "JSON 处理异常: " + e.getMessage(), e);
        }
        return jsonObject;
    }

    /**
     * Gson实现 由类转为JSONObject
     * @param src   类
     * @return      org.json.JSONObject
     */
    public static org.json.JSONObject toJsonGson(Object src) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // 转换为 JSON 字符串
        String jsonString = gson.toJson(src);
        try {
            // 创建 JSONObject
            return new org.json.JSONObject(jsonString);
        } catch (org.json.JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * FastJson实现 由类转为fastjson.JSONObject
     * @param object            类
     * @param prettyFormat      是否转为Json字符串
     * @return  com.alibaba.fastjson.JSONObject
     */
    public static com.alibaba.fastjson.JSONObject toFastJson(Object object, boolean prettyFormat) {
        // 转换为格式化的 JSON 字符串
        String jsonString = JSON.toJSONString(object, prettyFormat);
        // 创建 JSONObject
        return JSON.parseObject(jsonString);
    }

    /**
     * 将Map转换为fastjson.JSONObject
     * @param map   要转换的Map对象
     * @return      fastjson.JSONObject
     */
    public static com.alibaba.fastjson.JSONObject mapToJson(Map<String, Object> map) {
        // 将Map转换为 JSONObject
        return (com.alibaba.fastjson.JSONObject) JSON.toJSON(map);
    }

    public static String toJsonString(com.alibaba.fastjson.JSONObject jsonObject) {
        try {
            // 使用格式化输出
            return JSON.toJSONString(jsonObject, true);
        } catch (com.alibaba.fastjson.JSONException e) {
            Log.e(TAG, "JSON 转换异常: " + e.getMessage(), e);
            return jsonObject.toJSONString();
        }
    }

    public static String mapToJsonString(Map<String, Object> map){
        return toJsonString(mapToJson(map));
    }

    public static String fastJsonToJsonString(com.alibaba.fastjson.JSONObject jsonObject) {
        return JSON.toJSONString(jsonObject);
    }

    public static String toJsonString(Object object) {
        try {
            // 使用格式化输出
            return JSON.toJSONString(toJson(object), true);
        } catch (com.alibaba.fastjson.JSONException e) {
            Log.e(TAG, "JSON 转换异常: " + e.getMessage(), e);
            return "";
        }
    }

    public static com.alibaba.fastjson.JSONObject toJson(Object object) {
        // 将当前对象转换为 JSONObject
        return (com.alibaba.fastjson.JSONObject) JSON.toJSON(object);
    }

    public static <T> String listToJsonString(List<T> list) {
        StringBuilder builder = new StringBuilder();
        for (Object object : list){
            builder.append(toJsonString(object));
        }
        return builder.toString();
    }

}
