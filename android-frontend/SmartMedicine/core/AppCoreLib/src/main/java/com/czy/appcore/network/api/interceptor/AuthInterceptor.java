package com.czy.appcore.network.api.interceptor;

import androidx.annotation.NonNull;

import com.czy.appcore.BaseConfig;
import com.czy.dal.ao.login.LoginTokenAo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private LoginTokenAo loginTokenAo = null;

    public void setLoginTokenAo(LoginTokenAo loginTokenAo){
        this.loginTokenAo = loginTokenAo;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // 如果 loginTokenAo 为 null，直接传递请求
        if (loginTokenAo == null || loginTokenAo.isEmpty()) {
            return chain.proceed(chain.request());
        }

        // 获取原始请求
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();

        // 检查 URL 是否包含 "/has-0!0-token"
        if (url.contains(BaseConfig.AUTH_TOKEN_PREFIX)) {
            // 构建新的 URL，去掉 "/has--token"
            String newUrl = url.replace(BaseConfig.AUTH_TOKEN_PREFIX, "");

            // 获取 token
            String accessToken = loginTokenAo.accessToken;
            String refreshToken = loginTokenAo.refreshToken;

            // 创建新的请求
            Request newRequest = originalRequest.newBuilder()
                    .url(newUrl) // 替换 URL
                    .header("accessToken", accessToken)
                    .header("refreshToken", refreshToken)
                    .build();

            return chain.proceed(newRequest);
        }

        // 对于其他请求，直接继续
        return chain.proceed(originalRequest);
    }
}
