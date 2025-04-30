package com.example.chattest.User;
import android.util.Log;

import com.example.chattest.Test.MyDebug;
import com.example.chattest.URL.UrlUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeartbeatTask {
    private static final String TAG = "Runtime";
    private final String SPRING_BOOT_HEART_URL;
    private Thread heartbeatThread;
    private boolean isRunning;


    public HeartbeatTask(int user_id) {
        SPRING_BOOT_HEART_URL = UrlUtil.Get_GET_heartbeatUrl(user_id);
    }


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
                            Thread.sleep(3000);
                            MyDebug.Print("休眠");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        continue;
                    }

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
                        Log.e(TAG, "Error sending heartbeat: " + e.getMessage());
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
}