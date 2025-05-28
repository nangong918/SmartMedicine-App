package com.czy.baseUtilsLib.json;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public interface BaseBean {

    default String toJsonString() {
        try {
            // 使用格式化输出
            return JSON.toJSONString(toJson(), true);
        } catch (JSONException e) {
            Log.e(BaseBean.class.getSimpleName(), "JSON 转换异常: " + e.getMessage(), e);
            return "";
        }
    }

    default JSONObject toJson() {
        // 将当前对象转换为 JSONObject
        return (JSONObject) JSON.toJSON(this);
    }

}
