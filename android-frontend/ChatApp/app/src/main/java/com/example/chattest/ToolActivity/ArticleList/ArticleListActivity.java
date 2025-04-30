package com.example.chattest.ToolActivity.ArticleList;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chattest.Home.ArticlePage.ArticlePage;
import com.example.chattest.ToolActivity.requestType.RequestArticleListType;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.SpaceItemDecoration;
import com.example.chattest.Utils.Type.R_Util;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.ActivityArticleListBinding;
import java.util.ArrayList;
import java.util.List;

public class ArticleListActivity extends AppCompatActivity implements ArticleListInterface{
    private ActivityArticleListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Init
        Init();

        //RecyclerView
        RecyclerMain();
    }

    //-----------------------Init-----------------------

    private void Init(){
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.connectText.setVisibility(View.GONE);
        articleListSendTypeList = new ArrayList<>();
        GetIntentParameter();
        requestArticle();
    }
    private int[] articleIdArray;
    private List<ArticleListSendType> articleListSendTypeList;
    private void GetIntentParameter(){
        // 获取传递的参数
        Intent intent = getIntent();
        articleIdArray = intent.getIntArrayExtra("list");
    }

    //------------------------HTTP + Request------------------------

    private final String ArticleListActivity_request_getArticle = "ArticleListActivity_request_getArticle";
    private void requestArticle(){
        if (articleIdArray != null && articleIdArray.length > 0){
            RequestArticleListType requestArticleListType = new RequestArticleListType(articleIdArray);
            R_dataType rDataType = new R_dataType(requestArticleListType);
            String jsonData = R_Util.R_JsonUtils.toJson(rDataType);
            RequestUtils requestUtils = new RequestUtils(
                    10 * 1000,ArticleListActivity_request_getArticle,"POST",
                    UrlUtil.Get_articleList(),jsonData);
            requestUtils.callback = this;
            requestUtils.StartThread();
        }
    }

    private void AnalysisRequestArticle(R_dataType rData){
        Object data = rData.getData();
        JSONObject ReceivedJson = (JSONObject)data;
        JSONArray dataArray = ReceivedJson.getJSONArray("data");
        for(int i = 0;i < dataArray.size();i++){
            ArticleListSendType type = new ArticleListSendType();
            JSONObject dataObject = dataArray.getJSONObject(i);
            type.id = dataObject.getInteger("id");
            type.title = dataObject.getString("title");
            type.content = dataObject.getString("content");
            type.authorId = dataObject.getInteger("authorId");
            type.authorName = dataObject.getString("authorName");
            type.articlePicture = dataObject.getBytes("articlePicture");
            type.authorPicture = dataObject.getBytes("authorPicture");
            type.time = dataObject.getString("time");
            articleListSendTypeList.add(type);
        }
        if(articleListSendTypeList.isEmpty()){
            binding.connectText.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
        else {
            ChangeSendTypeIntoItem();
        }
    }

    //------------------------Tool------------------------

    @SuppressLint("NotifyDataSetChanged")
    private void ChangeSendTypeIntoItem(){
        articleAdapter.articleItemList = new ArrayList<>();
        for(ArticleListSendType sendType : this.articleListSendTypeList){
            ArticleAdapter.ArticleItem newItem = new ArticleAdapter.ArticleItem();
            newItem.ArticlePicture = sendType.articlePicture;
            newItem.AuthorPicture = sendType.authorPicture;
            newItem.Title = sendType.title;
            newItem.UserName = sendType.authorName;
            newItem.Time = sendType.time;
            newItem.Content = sendType.content;
            articleAdapter.articleItemList.add(newItem);
        }
        if(!articleAdapter.articleItemList.isEmpty()){
            // 通知适配器数据已更改
            articleAdapter.notifyDataSetChanged();
        }
    }

    //------------------------Listener------------------------

    private void SetListener(){

    }

    //-----------------------Recycler-----------------------

    public static List<ArticleAdapter.ArticleItem> articleItemList;
    private ArticleAdapter articleAdapter;

    private void RecyclerMain(){
        articleItemList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(articleItemList,this);
        binding.ArticleListtRecyclerView.setAdapter(articleAdapter);
        //set spacing
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(0,0,10,10);
        binding.ArticleListtRecyclerView.addItemDecoration(itemDecoration);
    }

    // --------------------clickListener--------------------

    @Override
    public void onListClickListener(int position) {
        Intent intent = new Intent(this, ArticlePage.class);
        // 添加要传递的参数
        intent.putExtra("position", position);
        intent.putExtra("class","ArticleList");
        startActivity(intent);
    }

    // --------------------Request--------------------

    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        if(callbackClass.equals(ArticleListActivity_request_getArticle)){
            binding.progressBar.setVisibility(View.GONE);
            AnalysisRequestArticle(rData);
        }
    }

    @Override
    public void onFailure(String callbackClass) {
        if(callbackClass.equals(ArticleListActivity_request_getArticle)){
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "请求失败！", Toast.LENGTH_SHORT).show();
        }
    }
}