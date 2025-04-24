package com.example.chattest.Chat.message;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSONObject;
import com.example.chattest.URL.UrlUtil;

public class SendMessage {
    private static final String API_URL = UrlUtil.GetDialogAI_url();

    public static String receivedMessage = "";

    private static void sendMessage(String text) {
        try {
            // 创建URL对象
            URL url = new URL(API_URL);

            // 创建连接对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为POST
            connection.setRequestMethod("POST");

            // 设置请求头部信息
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            // 启用输出流，并设置请求体数据
            connection.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", text);
            String jsonString = jsonObject.toJSONString();
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
            // 获取响应状态码
            int responseCode = connection.getResponseCode();
            // 根据状态码判断请求是否成功
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取服务器返回的响应数据
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 处理服务器返回的JSON数据
                String jsonResponse = response.toString();

                // 解析JSON数据
                JSONObject jsonObject_rec = JSONObject.parseObject(jsonResponse);

                String reply = jsonObject_rec.getString("reply");
                int success = jsonObject_rec.getIntValue("success");

                if(success == 1){
                    receivedMessage = reply;
                }
                else{
                    receivedMessage = "";
                }
            } else {
                // 请求失败
                Log.d("Runtime", "Failed to send message. Response code: " + responseCode);
            }

            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Android 4.0 之后不能在主线程中请求HTTP请求
    public static void StartThread(String text){

        new Thread(new Runnable(){
            @Override
            public void run() {
                receivedMessage = "";
                try {
                    SendMessage.sendMessage(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}