package com.czy.appcore.network.api.api;


import com.czy.appcore.network.api.interceptor.AuthInterceptor;
import com.czy.baseUtilsLib.network.BaseApiRequestProvider;
import com.czy.baseUtilsLib.network.LoggingInterceptor;
import com.czy.dal.ao.login.LoginTokenAo;

import java.util.LinkedList;
import java.util.List;

import okhttp3.Interceptor;

/**
 * @author 13225
 */
public class ApiRequestProvider extends BaseApiRequestProvider {

    private static volatile ApiRequest apiRequest;
    private static volatile AuthInterceptor authInterceptor;

    private static final long CONNECT_TIMEOUT = 10;
    private static final long READ_TIMEOUT = 10;
    private static final long WRITE_TIMEOUT = 10;
    //响应处理超时时间：30秒
    private static final long CALL_TIMEOUT = 30;

    public static ApiRequest getApiRequest() {
        if (apiRequest == null) {
            synchronized (ApiRequestProvider.class) {
                if (apiRequest == null) {
                    apiRequest = createApiRequest(
                            ApiRequest.class,
                            ApiUrlConfig.getUrl(),
                            CONNECT_TIMEOUT,
                            READ_TIMEOUT,
                            WRITE_TIMEOUT,
                            CALL_TIMEOUT,
                            getInterceptors()
                            );
                }
            }
        }
        return apiRequest;
    }

    public static AuthInterceptor getAuthInterceptor(LoginTokenAo loginTokenAo){
        if (authInterceptor == null){
            authInterceptor = new AuthInterceptor();
            authInterceptor.setLoginTokenAo(loginTokenAo);
        }
        else {
            authInterceptor.setLoginTokenAo(loginTokenAo);
        }
        return authInterceptor;
    }

    public static List<Interceptor> getInterceptors() {
        List<Interceptor> interceptors = new LinkedList<>();
        interceptors.add(getAuthInterceptor(null));
        interceptors.add(new LoggingInterceptor(true));
//        interceptors.add(new EncryptionInterceptor());
        return interceptors;
    }

}
