package com.czy.appcore;

public class BaseConfig extends com.czy.baseUtilsLib.config.BaseConfig {
    public static final String DNS = "192.168.1.2";// 192.168.101.176  192.168.1.2
    // netty socket：netty长连接的端口号，后期改为http请求获取可用端口号，而不是写死在前端
    public static final int webSocketPort = 30020;
    // local Address：Spring Cloud Gateway的网关端口号，在没有DNS的时候使用它来统一后端的一系列微服务
    private static final String LOCAL_ADDRESS = DNS + ":8888/";
    // Local Url
    public static final String LOCAL_URL = "http://" + LOCAL_ADDRESS;
    // Test Url
    public static final String TEST_URL = "https://smartmedicine/test/";
    // Production Url
    public static final String PRODUCTION_URL = "https://smartmedicine/api/";

    // Local webSocket Url
    public static final String LOCAL_WEB_SOCKET_URL = "ws://" + LOCAL_ADDRESS + "ws/";
    public static final String TEST_WEB_SOCKET_URL = "ws://smartmedicine/ws/";
    public static final String PRODUCTION_WEB_SOCKET_URL = "ws://smartmedicine/ws/";

    // 包名
    public static final String PACKAGE_NAME = "com.czy.smartmedicine";

    // 验证码长度
    public static final int V_CODE_LENGTH = 6;
    // 电话前缀
    public static final String PHONE_PREFIX = "1";
    // 电话长度
    public static final int PHONE_LENGTH = 11;

    // StartActivity 等待时长
    public static final int DELAY_TIME = 2000;

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
}
