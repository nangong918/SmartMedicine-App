package com.czy.appcore;

public class BaseConfig extends com.czy.baseUtilsLib.config.BaseConfig {
    public static final String DNS = "192.168.1.5";// 192.168.101.176  192.168.1.5
    public static final int webSocketPort = 30020;
    // local Address
    private static final String LOCAL_ADDRESS = DNS + ":60030/";
    // Local Url
    public static final String LOCAL_URL = "http://" + LOCAL_ADDRESS;
    // Test Url
    public static final String TEST_URL = "https://easysocial/test/";
    // Production Url
    public static final String PRODUCTION_URL = "https://easysocial/api/";

    // Local webSocket Url
    public static final String LOCAL_WEB_SOCKET_URL = "ws://" + LOCAL_ADDRESS + "ws/";
    public static final String TEST_WEB_SOCKET_URL = "ws://easysocial/ws/";
    public static final String PRODUCTION_WEB_SOCKET_URL = "ws://easysocial/ws/";

    // 包名
    public static final String PACKAGE_NAME = "com.czy.easysocial";

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
}

/**
 * 数据：
 * 1.Socket数据：Activity存活的时候直接交给Activity，非存活的时候交给SocketMessageQueue
 * 2.Http数据：首次连接Socket之后调用Http请求
 * 3.外存数据：SQLite数据
 * 4.缓存池数据(Redis：LruCache)：保存最近的30条数据；设置一定时间后后过时
 *
 * 初始化逻辑：
 * 从缓存读取是否首次打开；首次打开：Http请求；非首次打开：Redis获取数据
 *
 * 上拉/缓存不足逻辑：
 * 检查当前联网状态：
 *      联网的情况下：从Redis获取数据，数据不足从Http获取
 *      断网的情况下：从Redis获取数据，数据不足从SQLite获取数据
 *
 * 数据源合并/持久化：
 *      1.SocketMessageQueue数据交给ChatListManager
 *      2.Http数据交给ChatListManager
 *
 * 组件：
 * 1.ChatListManager：用于合并数据，并缓存数据；
 * 2.SocketMessageQueue将异步的Socket消息存储，防止线程高并发
 * 3.DataBaseRepository：用于获取SQLite数据；持久化数据
 * 4.ApiRequestImpl：用于Http请求
 *
 * 实现：
 * 首次打开Http数据拉取交给ChatListManager进行数据合并，合并完成通知Redis（30条数据）
 * 收到Socket数据交给SocketMessageQueue，SocketQueue直接交给ChatListManager进行数据合并，合并完成更新Redis（30条数据）
 * 数据源不足的时候，从Http/SQLite获取数据，数据交给ChatListManager进行合并
 * 数据根据定时规则存储到SQLite
 */

// Fix Bug:
//  7.用户Activity首次打开应该进行Http请求；ChatListManager；ChatActivity首次打开缓存；消息缓存池SocketMessageQueue
//  8.MainApplication应当存储每个用户30条消息左右的最新数据，在往上就应该从Android的SQLite拉取数据
//  9.测试重连机制；标准：后端宕机之后前端尝试从新连接，直到与后端连接成功
//  10.已读功能
//  11.图解IM系统

// T odo:
//  2.用对象图理清后端的数据类型对象转换
//  3.图解IM系统
//  4.使用RabbitMQ
//  5.使用WebFlux
//  6.看下载的PDF

// other:
// 图解IM系统
// 图解支付系统
// 图解推荐系统
