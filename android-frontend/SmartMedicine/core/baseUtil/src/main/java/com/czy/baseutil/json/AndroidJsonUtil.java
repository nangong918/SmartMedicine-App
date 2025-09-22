package com.czy.baseutil.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class AndroidJsonUtil {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create(); // 创建 Gson 实例

    /**
     * 将 Map<String, Object> 转换为 JSON 格式字符串
     *
     * @param map 要转换的 Map
     * @return JSON 格式字符串
     */
    public static String mapToJson(Map<String, Object> map) {
        if (map == null) {
            return "{}"; // 返回空 JSON 对象
        }
        return GSON.toJson(map); // 使用 Gson 转换
    }

}
