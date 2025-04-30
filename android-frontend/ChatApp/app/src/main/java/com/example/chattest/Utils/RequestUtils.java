package com.example.chattest.Utils;
import android.os.AsyncTask;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.Utils.Type.R_dataType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class RequestUtils extends AsyncTask<String, Void, String>{

    public boolean flag = false;
    public CallBackInterface callback;
    private int timeOut;
    public String Request_class;
    private String requestType;
    private String requestUrl;
    private String RequestBody;

    public RequestUtils(int timeOut,String Request_class,String requestType,String requestUrl){
        this.timeOut = timeOut;
        this.Request_class = Request_class;
        assert requestType.equals("POST") || requestType.equals("GET");
        this.requestType = requestType;
        this.requestUrl = requestUrl;
    }

    public RequestUtils(int timeOut,String Request_class,String requestType,String requestUrl,String RequestBody){
        this.timeOut = timeOut;
        this.Request_class = Request_class;
        assert requestType.equals("POST") || requestType.equals("GET");
        this.requestType = requestType;
        this.requestUrl = requestUrl;
        this.RequestBody = RequestBody;
    }

    public RequestUtils(int timeOut,String Request_class,String requestType,String requestUrl,String RequestBody,CallBackInterface callback){
        this.timeOut = timeOut;
        this.Request_class = Request_class;
        assert requestType.equals("POST") || requestType.equals("GET");
        this.requestType = requestType;
        this.requestUrl = requestUrl;
        this.RequestBody = RequestBody;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;

        try {
            String urlString = params[0];
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeOut);
            connection.setReadTimeout(timeOut);

            if(this.requestType.equals("POST")){
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
                outputStream.write(this.RequestBody.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            else {
                connection.setRequestMethod("GET");
            }

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
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject_rec = JSONObject.parseObject(result);
            R_dataType rData = new R_dataType(jsonObject_rec);
            flag = rData.getFlag();

            if (!flag) {
                // 请求失败
                if (callback != null) {
                    callback.onFailure(this.Request_class);
                }
                return;
            }

            if (callback != null) {
                callback.onSuccess(this.Request_class, rData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void StartThread() {
        this.execute(requestUrl);
    }

    // 取消当前请求
    public void cancelRequest() {
        cancel(true);
    }
}
