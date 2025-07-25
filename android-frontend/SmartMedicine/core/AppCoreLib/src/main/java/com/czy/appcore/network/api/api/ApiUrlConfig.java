package com.czy.appcore.network.api.api;



import com.czy.appcore.BaseConfig;
import com.czy.baseUtilsLib.debug.DebugEnvironment;

public class ApiUrlConfig {

    private static final String TAG = ApiUrlConfig.class.getSimpleName();

    // 获取url
    private static String getMainUrl() {
        return switch (DebugEnvironment.projectEnvironment) {
            case LOCAL -> BaseConfig.LOCAL_URL;
            case TEST -> BaseConfig.TEST_URL;
            case PRODUCTION, STAGING -> BaseConfig.PRODUCTION_URL;
            // 默认情况
        };
    }

    // 交给H5调用
    public static String getMainApiUrlH5(){
        String apiUrl = getMainUrl() + "/api";
        DebugEnvironment.logR("H5访问的ApiUrl为:" + apiUrl);
        return apiUrl;
    }

    // Retrofit2 的baseUlr 必须以 /结束，不然会抛出一个IllegalArgumentException
    public static String getUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append(ApiUrlConfig.getMainUrl())
                //.append("/api/")
                .append("/")
        ;
        DebugEnvironment.logR("builder:" + builder);
        return builder.toString();
    }

}
