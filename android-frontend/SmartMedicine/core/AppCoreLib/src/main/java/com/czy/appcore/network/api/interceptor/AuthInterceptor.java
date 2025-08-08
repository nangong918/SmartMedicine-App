package com.czy.appcore.network.api.interceptor;

import androidx.annotation.NonNull;

import com.czy.appcore.BaseConfig;
import com.czy.dal.ao.login.LoginTokenAo;

import java.io.IOException;
import java.util.Optional;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final static String TAG = AuthInterceptor.class.getName();

    private LoginTokenAo loginTokenAo = null;

    public void setLoginTokenAo(LoginTokenAo loginTokenAo){
        this.loginTokenAo = loginTokenAo;
    }

    public boolean isLogin(){
        return loginTokenAo != null && !loginTokenAo.isEmpty();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        // 获取原始请求
        Request originalRequest = chain.request();
        String url = originalRequest.url().toString();

        // 检查 URL 是否包含 "/has-0!0-token"
        if (url.contains(BaseConfig.AUTH_TOKEN_PREFIX)) {
            // 构建新的 URL，去掉 "/has--token"
            String newUrl = url.replace(BaseConfig.AUTH_TOKEN_PREFIX, "");

            // 获取 token
            String accessToken = Optional.ofNullable(loginTokenAo)
                    .map(ao -> ao.accessToken)
                    .orElse("");
            String refreshToken = Optional.ofNullable(loginTokenAo)
                    .map(ao -> ao.refreshToken)
                    .orElse("");

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
// todo bug: 1.数组越界 2.返回空指针问题 3.拦截器失效问题 4.未存储post数据
