package com.czy.baseutil.network;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TimeoutInterceptor implements Interceptor {
    private static final String TAG = TimeoutInterceptor.class.getName();
    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            // 执行请求
            return chain.proceed(request);
        } catch (IOException e) {
            // 区分不同类型的超时
            String timeoutType = getTimeoutType(e);
            Log.e(TAG, "请求超时 - " + timeoutType + ", URL: " + request.url());

            // 可以在这里添加自定义处理，如：
            // 1. 记录超时日志到本地
            // 2. 根据超时类型进行不同的重试策略
            // 3. 抛出自定义异常供上层处理
            throw new IOException(timeoutType, e);
        }
    }

    /**
     * 区分超时类型
     * OkHttp的超时异常消息格式通常包含超时类型信息
     */
    private String getTimeoutType(IOException e) {
        String message = e.getMessage();
        if (message == null) {
            return "未知超时";
        }

        // 1. 连接超时特征：包含"failed to connect"或"connect timed out"
        if (message.contains("failed to connect") || message.contains("connect timed out")) {
            return "连接超时(connectTimeout)";
        }

        // 2. 读取超时特征：包含"read timed out"或"timeout reading"
        if (message.contains("read timed out") || message.contains("timeout reading")) {
            return "读取超时(readTimeout)";
        }

        // 3. 写入超时特征：包含"write timed out"或"timeout writing"
        if (message.contains("write timed out") || message.contains("timeout writing")) {
            return "写入超时(writeTimeout)";
        }

        // 4. 调用超时特征：包含"call timed out"（总超时）
        if (message.contains("call timed out")) {
            return "调用超时(callTimeout)";
        }

        // 5. connection closed
        if (message.contains("connection closed")) {
            return "连接已关闭(connectionClosed)";
        }
        else {
            return "未知超时类型: " + message;
        }
    }

}
