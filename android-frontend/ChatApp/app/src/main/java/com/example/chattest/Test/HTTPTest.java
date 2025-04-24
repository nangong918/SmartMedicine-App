package com.example.chattest.Test;

import com.alibaba.fastjson.JSONObject;
import com.example.chattest.URL.UrlUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*public class HTTPTest {

    private static final String API_URL = SaveUrl.GetRecommendService(SaveUrl.GetIP()) + "/articleGet";

    public static byte[] Image;
    public static String Context;
    public static boolean flag = false;
    public static Callback callback;

    public static void GetInformation(int id){
        try{
            String id_URL = API_URL + "/" + id;
            // 创建URL对象
            URL url = new URL(id_URL);
            Log.d("Runtime", "00000000000000000000");
            // 创建连接对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);

            Log.d("Runtime", "10000000000000000000");

            // 设置请求方法为POST
            connection.setRequestMethod("GET");

            Log.d("Runtime", "20000000000000000000");

            int responseCode = connection.getResponseCode();

            Log.d("Runtime", "responseCode"+responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK){
                // 读取响应数据
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                Log.d("Runtime", "1111111111111111111");

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                Log.d("Runtime", "222222222222222222222");

                // 处理响应数据，可以根据实际需求进行相应的操作
                String responseData = response.toString();

                JSONObject jsonObject_rec = JSONObject.parseObject(responseData);

                flag = jsonObject_rec.getBoolean("flag");

                Log.d("Runtime", "33333333333333333333333");

                if(!flag){
                    // 请求失败
                    if (callback != null) {
                        callback.onFailure();
                    }
                    return;
                }

                if (callback != null) {
                    callback.onSuccess();
                }

                // 从顶层 JSON 对象中获取 "data" 字段
                JSONObject dataObject = jsonObject_rec.getJSONObject("data");

                // 从 "data" 字段中获取 "msg" 字段的值
                Context = dataObject.getString("msg");
                Image = dataObject.getBytes("articlePic");
            }
            else {
                flag = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void StartThread(int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTPTest.GetInformation(id);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}*/

import android.os.AsyncTask;

public class HTTPTest {

    private static final String API_URL = UrlUtil.Get_SpringBoot_url(UrlUtil.GetIP()) + "/articleGet";

    public static byte[] Image;
    public static String Context;
    public static boolean flag = false;
    public static testCallback callback;

    public static void GetInformation(int id) {
        String id_URL = API_URL + "/" + id;
        new GetInformationTask(). execute(id_URL);
    }

    private static class GetInformationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject_rec = JSONObject.parseObject(result);
                flag = jsonObject_rec.getBoolean("flag");

                if (!flag) {
                    // 请求失败
                    if (callback != null) {
                        callback.onFailure();
                    }
                    return;
                }

                JSONObject dataObject = jsonObject_rec.getJSONObject("data");
                Context = dataObject.getString("msg");
                Image = dataObject.getBytes("articlePic");

                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void StartThread(int id) {
        GetInformation(id);
    }
}
