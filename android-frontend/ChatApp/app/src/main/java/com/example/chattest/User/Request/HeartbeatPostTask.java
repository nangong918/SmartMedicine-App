package com.example.chattest.User.Request;

import android.util.Log;

import com.example.chattest.Home.HomeFragment;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.User.User;
import com.example.chattest.Utils.Type.R_Util;
import com.example.chattest.Utils.Type.R_dataType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeartbeatPostTask {
    private Thread heartbeatPostThread;
    private boolean isRunning;
    private final String SPRING_BOOT_HEART_POST_URL;
    private long sleepTime = 5 * 1000;
    public String requestBody;

    public HeartbeatPostTask() {
        SPRING_BOOT_HEART_POST_URL = UrlUtil.Get_GET_heartbeat_Post_Url();
    }

    public void start(){
        if (isRunning) {
            return;
        }
        isRunning = true;
        heartbeatPostThread = new Thread(new Runnable() {
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

                    if(HomeFragment.backArticleBehaviorSet != null && !HomeFragment.backArticleBehaviorSet.isEmpty()){
                        MyDebug.Print("测试：backArticleBehaviorSet:"+HomeFragment.backArticleBehaviorSet.size());
                        try {
                            // 创建URL对象
                            URL url = new URL(SPRING_BOOT_HEART_POST_URL);

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            //请求体
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);
                            //Post方式不能缓存,需手动设置为false
                            connection.setUseCaches(false);

                            // 设置请求体
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.connect();

                            //BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                            OutputStream outputStream = connection.getOutputStream();

                            R_dataType rDataType = new R_dataType(HomeFragment.backArticleBehaviorSet);
                            String jsonData = R_Util.R_JsonUtils.toJson(rDataType);

                            assert jsonData != null;
                            outputStream.write(jsonData.getBytes());
                            outputStream.flush();
                            outputStream.close();

                            // 发送请求
                            connection.getResponseCode(); // 不需要处理响应
                            MyDebug.Print("心跳请求：发送行为列表");
                            // 关闭连接
                            connection.disconnect();

                            // 休眠5秒
                            Thread.sleep(sleepTime);
                        } catch (IOException | InterruptedException e) {
                            MyDebug.Print("Error sending heartbeat: " + e.getMessage());
                        }
                    }
                    else {
                        // 休眠3秒
                        try {
                            Thread.sleep(3000);
                            MyDebug.Print("休眠");
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        continue;
                    }
                }
            }
        });
        heartbeatPostThread.start();
    }

    public void stop() {
        isRunning = false;
        if (heartbeatPostThread != null) {
            heartbeatPostThread.interrupt();
            heartbeatPostThread = null;
        }
    }
}
