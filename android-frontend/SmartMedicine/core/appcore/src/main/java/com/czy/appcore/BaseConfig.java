package com.czy.appcore;

import android.Manifest;

public class BaseConfig extends com.czy.baseutil.config.BaseConfig {
    public static final String LOCAL_DNS = "192.168.1.2";// 192.168.101.176  192.168.1.2
    /**
     * Android 无法直接解析http://smart-medicine 因为Android的hosts文件没有配置其IP解析。如果要使用有两个方案
     * 1. 配置hosts文件
     * 2. 创建局域网DNS服务器 设置 DHCP 服务器
     */
    public static final String TEST_DNS = "smart-medicine";
    // netty socket：netty长连接的端口号，后期改为http请求获取可用端口号，而不是写死在前端
    public static final int webSocketPort = 30020;
    // local Address：Spring Cloud Gateway的网关端口号，在没有DNS的时候使用它来统一后端的一系列微服务
    private static final String LOCAL_ADDRESS = LOCAL_DNS + ":8888/";
    // Local Url
    public static final String LOCAL_URL = "http://" + LOCAL_ADDRESS;
    // Test Url
    public static final String TEST_URL = "http://" + TEST_DNS;
    // Production Url
    public static final String PRODUCTION_URL = "https://smart-medicine/";

    // 包名
    public static final String PACKAGE_NAME = "com.czy.smartmedicine";

    // 验证码长度
    public static final int V_CODE_LENGTH = 6;
    // 电话前缀
    public static final String PHONE_PREFIX = "1";
    // 电话长度
    public static final int PHONE_LENGTH = 11;
    // 搜索字段最小长度
    public static final int SEARCH_FIELD_MIN_LENGTH = 2;
    // 搜索字段最大长度
    public static final int SEARCH_FIELD_MAX_LENGTH = 15;

    // StartActivity 等待时长
    public static final int DELAY_TIME = 1200;
    // 默认一次获取消息数量
    public static final int DEFAULT_MESSAGE_FETCH_COUNT = 20;

    // SocketMessageQueue
    // 消息队列决定持久化阈值
    public static final long SOCKET_QUEUE_MAX_QUEUE_SIZE = 30L;
    // 消息队列持久化检查时间 : 30s
    public static final long SOCKET_QUEUE_PERSISTENCE_INTERVAL = 3000_0L;

    // 图片压缩 400 * 400 = 640 KB
    public static final int BITMAP_MAX_SIZE = 400;
    // 头像最大大小 200 * 200 = 160 KB
    public static final int BITMAP_MAX_SIZE_AVATAR = 200;
    // 手机号前缀
    public static String phonePrefix = "+86";

    // 请求是否加认证token前缀 最后要在拦截器检查去掉; 定义一些不像url的命名避免出现与后端路由重合
    public static final String AUTH_TOKEN_PREFIX = "/has-0!0-token";


    // permission
    public static final String[] MUST_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
    };
    public static final String[] NOT_MUST_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
}
