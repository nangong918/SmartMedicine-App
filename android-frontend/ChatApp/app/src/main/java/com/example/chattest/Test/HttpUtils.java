package com.example.chattest.Test;

import com.alibaba.fastjson.JSONObject;
import com.example.chattest.URL.UrlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static String sendGetRequest(int id) {

        boolean flag;

        String API_URL = UrlUtil.Get_SpringBoot_url(UrlUtil.GetIP()) + "/articleGet";
        String urlString = API_URL + "/" + id;
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // 处理响应数据，可以根据实际需求进行相应的操作
            String responseData = response.toString();

            JSONObject jsonObject_rec = JSONObject.parseObject(responseData);

            flag = jsonObject_rec.getBoolean("flag");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return response.toString();
    }
}
