package com.czy.baseUtilsLib.network;


import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LoggingInterceptor implements Interceptor {

    private static final String TAG = Interceptor.class.getSimpleName();

    private boolean isShowHeader = true;

    public LoggingInterceptor(){
    }

    public LoggingInterceptor(boolean isShowHeader){
        this.isShowHeader = isShowHeader;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        long startTime = System.nanoTime();

        // 打印请求信息
        Log.d(TAG,"\n\n----> Request");
        Log.d(TAG,"Request URL: " + request.url());
        Log.d(TAG,"Method: " + request.method());

        if (this.isShowHeader){
            Log.d(TAG,"Request Headers: " + request.headers());
        }

        if(request.body() != null){
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            String requestBodyString = buffer.readUtf8();

            Log.d(TAG,"Request Content Type: " + request.body().contentType());
            Log.i(TAG, "\n\n----> RequestBody -> \n" + getJsonObject(requestBodyString, request.body().contentType()));
        }
        else {
            Log.d(TAG,"request.body() == null");
        }

        try (Response response = chain.proceed(request)) {

            long endTime = System.nanoTime();

            // 打印响应信息
            Log.d(TAG, "\n\n<---- Response (" + (endTime - startTime) / 1e6d + "ms)");
            Log.d(TAG, "Response URL: " + request.url()); // 打印请求的 URL
            Log.d(TAG, "Status Code: " + response.code());

            if (this.isShowHeader) {
                Log.d(TAG, "Response Headers: " + response.headers());
            }

            ResponseBody responseBody = response.body();
            String responseBodyString;
            if (responseBody != null) {
                responseBodyString = responseBody.string();

                Log.i(TAG, "\n\n<---- ResponseBody: \n" + getJsonObject(responseBodyString, response.headers()));
            } else {
                responseBodyString = "";
            }

            return response.newBuilder()
                    .body(ResponseBody.create(MediaType.parse("UTF-8"), responseBodyString))
                    .build();
        }
    }

    // TODO Form-data也需要拦截器

    // 解决非Json类型强行解析出现错误的问题
    private static String getJsonObject(String jsonString, Headers headers) {
        // 从请求头中获取 Content-Type 检查是否为 application/json
        String contentType = headers == null ? null : headers.get("Content-Type");
        boolean isJsonRequest = contentType != null && contentType.contains("application/json");

        // 检查是否为有效的 JSON 字符串
        if (isJsonRequest) {
            try {
                JSONObject jsonObject = JSON.parseObject(jsonString);
                return JSON.toJSONString(jsonObject, true);
            } catch (JSONException e) {
                return jsonString; // 返回原始字符串
            }
        } else {
            Log.e(TAG, "Non-JSON content");// 返回非 JSON 内容提示
            return "";
        }
    }

    private static String getJsonObject(String jsonString, MediaType mediaType) {
        // 从请求头中获取 Content-Type 检查是否为 application/json
        String contentType = mediaType == null ? null : mediaType.toString();
        boolean isJsonRequest = contentType != null && contentType.contains("application/json");

        // 检查是否为有效的 JSON 字符串
        if (isJsonRequest) {
            try {
                JSONObject jsonObject = JSON.parseObject(jsonString);
                return JSON.toJSONString(jsonObject, true);
            } catch (JSONException e) {
                return jsonString; // 返回原始字符串
            }
        } else {
            Log.e(TAG, "Non-JSON content");// 返回非 JSON 内容提示
            return "";
        }
    }

}
