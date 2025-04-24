package com.example.chattest.User.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.chattest.Test.MyDebug;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.User.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeartBeatService extends Service {
    public HeartBeatService() {

    }
    private boolean isRunning;
    private Thread heartbeatThread;


    //-------------------------OnCreate-------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        Init();
    }

    private void Init(){
        this.isRunning = false;
    }

    //-------------------------Start-------------------------

    public void start() {
        if (isRunning) {
            return;
        }

        isRunning = true;
        heartbeatThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    if (!User.Logged) {
                        // 休眠3秒
                        try {
                            Thread.sleep(HEARTBEAT_INTERVAL);
                            MyDebug.Print("休眠");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        continue;
                    }

                    try {
                        String SPRING_BOOT_HEART_URL = UrlUtil.Get_GET_heartbeatUrl(User.user_id);
                        // 创建URL对象
                        URL url = new URL(SPRING_BOOT_HEART_URL);

                        // 打开HTTP连接
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        // 发送请求
                        connection.getResponseCode(); // 不需要处理响应
                        MyDebug.Print("心跳请求：" + User.user_id + "在线");
                        // 关闭连接
                        connection.disconnect();

                        // 休眠5秒
                        Thread.sleep(HEARTBEAT_INTERVAL);
                    } catch (IOException | InterruptedException e) {
                        Log.e("Runtime", "Error sending heartbeat: " + e.getMessage());
                    }
                }
            }
        });
        heartbeatThread.start();
    }


    public void stop() {
        isRunning = false;
        if (heartbeatThread != null) {
            heartbeatThread.interrupt();
            heartbeatThread = null;
        }
    }

    //每次启动都会创建一个新的线程
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start();
        return START_REDELIVER_INTENT;
    }

    private static final long HEARTBEAT_INTERVAL = 5 * 1000; // 心跳间隔时间，单位为毫秒

    public void sendHeartbeatRequest() {
        // 在这里执行发送心跳请求的逻辑
        String SPRING_BOOT_HEART_URL = UrlUtil.Get_GET_heartbeatUrl(User.user_id);
        try {
            // 创建URL对象
            URL url = new URL(SPRING_BOOT_HEART_URL);

            // 打开HTTP连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 发送请求
            connection.getResponseCode(); // 不需要处理响应
            MyDebug.Print("心跳请求：" + User.user_id + "在线");
            // 关闭连接
            connection.disconnect();

            // 休眠5秒
            Thread.sleep(5000);
        } catch (IOException | InterruptedException e) {
            Log.e("Runtime", "Error sending heartbeat: " + e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }
}