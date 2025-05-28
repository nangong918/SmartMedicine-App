package com.czy.baseUtilsLib.network;


import java.io.File;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.BuildConfig;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseApiRequestProvider {

    /**
     * 创建 API 请求的 Retrofit 实例
     *
     * @param apiClass        API 接口的类类型
     * @param mainUrl         服务器的基础 URL
     * @param connectTimeOut  连接超时时间（秒）
     * @param readTimeOut     读取超时时间（秒）
     * @param writeTimeOut    写入超时时间（秒）
     * @param callTimeOut     调用超时时间（秒）
     * @param <T>            API 接口的类型
     * @return               返回指定 API 接口的实例
     */
    protected static <T> T createApiRequest(Class<T> apiClass, String mainUrl,
                                          long connectTimeOut, long readTimeOut, long writeTimeOut, long callTimeOut) {
        OkHttpClient uploadOkHttpClient = createUploadOkHttpClient(connectTimeOut,readTimeOut,writeTimeOut,callTimeOut);

        return new Retrofit.Builder()
                .baseUrl(mainUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Gson的序列化和反序列化
                .client(uploadOkHttpClient)
                .build()
                .create(apiClass); // 使用传入的 Class 对象
    }

    // 新增拦截器
    protected static <T> T createApiRequest(Class<T> apiClass, String mainUrl,
                                            long connectTimeOut, long readTimeOut, long writeTimeOut, long callTimeOut,
                                            List<Interceptor> interceptors) {
        OkHttpClient uploadOkHttpClient = createUploadOkHttpClient(connectTimeOut,readTimeOut,writeTimeOut,callTimeOut,interceptors);

        return new Retrofit.Builder()
                .baseUrl(mainUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  // Gson的序列化和反序列化
                .client(uploadOkHttpClient)
                .build()
                .create(apiClass); // 使用传入的 Class 对象
    }


    public static OkHttpClient createUploadOkHttpClient(long connectTimeOut, long readTimeOut, long writeTimeOut, long callTimeOut) {
        // 创建io缓冲区
        File cacheFile = getCacheDir();
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb 缓存的大小

        // 创建日志拦截器实例
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        // 根据构建类型设置日志级别
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 记录请求和响应的完整内容
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE); // 不记录任何日志
        }

        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)/*不重复请求*/
                // 超时时间配置
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                .callTimeout(callTimeOut, TimeUnit.SECONDS)
                // 添加缓存
                .cache(cache)
                // 输出拦截器
                .addInterceptor(new LoggingInterceptor(true))
                // Okhttp3日志拦截器
                .addInterceptor(loggingInterceptor)
                // 代理
                .proxy(Proxy.NO_PROXY)
                .build();
    }

    // 新增拦截器
    public static OkHttpClient createUploadOkHttpClient(long connectTimeOut, long readTimeOut, long writeTimeOut, long callTimeOut,
                                                        List<Interceptor> interceptors) {
        // 创建io缓冲区
        File cacheFile = getCacheDir();
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); // 50Mb 缓存的大小

        // 创建日志拦截器实例
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

        // 根据构建类型设置日志级别
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 记录请求和响应的完整内容
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE); // 不记录任何日志
        }

        // 创建OkHttpClient.Builder
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false) // 不重复请求
                .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                .callTimeout(callTimeOut, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(loggingInterceptor) // Okhttp3日志拦截器
                .proxy(Proxy.NO_PROXY);

        // 添加传入的拦截器
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }

        return builder.build();
    }

    // 自定义缓存目录
    private static File getCacheDir() {
        // 使用临时目录
        return new File(System.getProperty("java.io.tmpdir"), "http-cache");
    }

}
