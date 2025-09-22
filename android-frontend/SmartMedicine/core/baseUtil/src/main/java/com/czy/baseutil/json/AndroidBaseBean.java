package com.czy.baseutil.json;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public interface AndroidBaseBean {

    Gson GSON = new GsonBuilder().setPrettyPrinting().create(); // 创建 Gson 实例

    default String toJsonString(@NonNull Gson gson){
        try {
            // 使用格式化输出
            return gson.toJson(toJson());
        } catch (Exception e) {
            Log.e(AndroidBaseBean.class.getSimpleName(), "JSON 转换异常: " + e.getMessage(), e);
            return "";
        }
    }

    default String toJsonString() {
        try {
            // 使用格式化输出
            return GSON.toJson(toJson());
        } catch (Exception e) {
            Log.e(AndroidBaseBean.class.getSimpleName(), "JSON 转换异常: " + e.getMessage(), e);
            return "";
        }
    }

    default JsonObject toJson() {
        // 将当前对象转换为 JsonObject
        return GSON.toJsonTree(this).getAsJsonObject();
    }

}
