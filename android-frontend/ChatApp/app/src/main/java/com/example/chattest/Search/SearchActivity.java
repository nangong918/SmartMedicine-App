package com.example.chattest.Search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chattest.Home.ArticlePage.ArticlePage;
import com.example.chattest.R;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.ToolActivity.ArticleList.ArticleListActivity;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.Utils.CallBackInterface;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.ActivityMainBinding;
import com.example.chattest.databinding.ActivitySearchBinding;

import java.util.Arrays;

public class SearchActivity extends AppCompatActivity implements CallBackInterface {

    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Init();

        Listener();
    }

    //---------------------Init---------------------
    private void Init(){
        binding.connectText.setVisibility(View.GONE);
    }

    //---------------------Listener---------------------

    private void Listener(){
        binding.layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = binding.editTextInput.getText().toString();
                if(!data.isEmpty()){
                    Search_request(data);
                }
            }
        });
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //---------------------Http request---------------------

    private final String requestClass = "SearchActivity";

    public void Search_request(String data){
        RequestUtils requestUtils = new RequestUtils(10 * 1000,requestClass,"GET", UrlUtil.Get_search_url(data));
        requestUtils.callback = this;
        requestUtils.StartThread();
    }

    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        if (callbackClass.equals(requestClass)) {
            Object data = rData.getData();
            JSONObject jsonObject = (JSONObject) data;
            JSONArray dataArray = jsonObject.getJSONArray("data");
            // 将JSONArray对象转换为int[]数组
            int[] intArray = new int[dataArray.size()];
            for (int i = 0; i < dataArray.size(); i++) {
                intArray[i] = (int)dataArray.get(i);
            }

            Intent intent = new Intent(this, ArticleListActivity.class);
            // 传递int[]数组作为参数
            intent.putExtra("list", intArray);

            startActivity(intent);
        }
    }

    @Override
    public void onFailure(String callbackClass) {
        if(callbackClass.equals(requestClass)){
            binding.connectText.setVisibility(View.VISIBLE);
            Toast.makeText(this, "未找到相关文章。", Toast.LENGTH_SHORT).show();
        }
    }
}