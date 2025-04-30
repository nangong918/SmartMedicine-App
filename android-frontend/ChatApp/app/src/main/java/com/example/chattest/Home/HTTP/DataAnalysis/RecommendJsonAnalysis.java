package com.example.chattest.Home.HTTP.DataAnalysis;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.chattest.Utils.Type.R_dataType;

import java.util.ArrayList;
import java.util.List;

public class RecommendJsonAnalysis {

    private final String dataKey = "data";
    private final String msgKey = "msg";
    private final String flagKey = "flag";
    private final String articlePictureKey = "articlePic";
    private final String authorPictureKey = "authorPic";
    private final String contentKey = "content";
    private final String titleKey = "title";
    private final String timeKey = "time";
    private final String authorIdKey = "authorId";
    private final String authorNameKey = "authorName";
    private final String articleIdKey = "articleId";
    private JSONObject receivedJson;

    public RecommendJsonAnalysis(JSONObject receivedJson){
        this.receivedJson = receivedJson;
    }
    public RecommendJsonAnalysis(){

    }

    public boolean GetFlag(){
        return this.receivedJson.getBoolean(flagKey);
    }

    public void GetArticleData(List<ArticleDataFormat> articleDataFormatList){
        JSONArray dataArray = this.receivedJson.getJSONArray(dataKey);
        for(int i = 0;i<dataArray.size();i++){
            JSONObject dataObject = dataArray.getJSONObject(i);
            JSONObject msg = dataObject.getJSONObject(msgKey);
            byte[] articlePic = dataObject.getBytes(articlePictureKey);
            byte[] authorPic = dataObject.getBytes(authorPictureKey);
            String content = msg.getString(contentKey);
            String title = msg.getString(titleKey);
            String time = msg.getString(timeKey);
            String authorId = msg.getString(authorIdKey);
            String authorName = msg.getString(authorNameKey);
            ArticleDataFormat dataFormat = new ArticleDataFormat();
            dataFormat.ArticlePicture = articlePic;
            dataFormat.AuthorFacePicture = authorPic;
            dataFormat.time = time;
            dataFormat.title = title;
            dataFormat.content = content;
            dataFormat.authorId = authorId;
            dataFormat.authorName = authorName;
            articleDataFormatList.add(dataFormat);
        }
    }

    public void Test(){
        Log.d("Runtime","Test");
    }

    public List<ArticleDataFormat> GetArticleData(R_dataType rData, String dataKey){
        JSONObject ReceivedJson = (JSONObject) rData.getData();
        List<ArticleDataFormat> list = new ArrayList<>();
        JSONArray dataArray = ReceivedJson.getJSONArray(dataKey);
        for(int i = 0;i<dataArray.size();i++){
            JSONObject dataObject = dataArray.getJSONObject(i);
            JSONObject msg = dataObject.getJSONObject(msgKey);
            byte[] articlePic = dataObject.getBytes(articlePictureKey);
            byte[] authorPic = dataObject.getBytes(authorPictureKey);
            String content = msg.getString(contentKey);
            String title = msg.getString(titleKey);
            String time = msg.getString(timeKey);
            String authorId = msg.getString(authorIdKey);
            String authorName = msg.getString(authorNameKey);
            String articleId = msg.getString(articleIdKey);
            ArticleDataFormat dataFormat = new ArticleDataFormat();
            dataFormat.ArticlePicture = articlePic;
            dataFormat.AuthorFacePicture = authorPic;
            dataFormat.time = time;
            dataFormat.title = title;
            dataFormat.content = content;
            dataFormat.authorId = authorId;
            dataFormat.authorName = authorName;
            dataFormat.articleId = articleId;
            list.add(dataFormat);
        }
        return list;
    }


}
