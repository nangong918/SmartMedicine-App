package com.example.chattest.Home.ArticlePage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chattest.Home.HomeFragment;
import com.example.chattest.Home.RecyclerView.HomeAdapter;
import com.example.chattest.R;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.ToolActivity.ArticleList.ArticleAdapter;
import com.example.chattest.ToolActivity.ArticleList.ArticleListActivity;
import com.example.chattest.Utils.ActivityBackFragment;
import com.example.chattest.Utils.ImageUtils;
import com.example.chattest.databinding.ActivityArticlePageBinding;

public class ArticlePage extends AppCompatActivity {

    private ActivityArticlePageBinding binding;
    private long clickInTime;
    private int cardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticlePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Init
        Init();

        //setClick
        setClickListener();
    }

    //------------------------------------Init---------------------------------------------------

    private void Init(){
        clickInTime = System.currentTimeMillis();
        GetIntentParameter();
    }

    private void GetIntentParameter(){
        // 获取传递的参数
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        int cardType = intent.getIntExtra("cardType", 0);
        int cardId = intent.getIntExtra("cardId", 0);
        boolean like = intent.getBooleanExtra("like",false);
        boolean star = intent.getBooleanExtra("star",false);
        String transmitClass = intent.getStringExtra("class");
        this.cardId = cardId;
        this.position = position;

        if (transmitClass == null) {
            transmitClass = "HomeFragment";
        }

        //初始化接口
        if (transmitClass.equals("HomeFragment")){
            activityBackFragment = HomeFragment.THIS;
        }

        if(!like){
            binding.favoriteImage.setImageResource(R.drawable.favorite_border);
            binding.favoriteImage.setColorFilter(R.color.green_500);
        }
        else {
            binding.favoriteImage.setImageResource(R.drawable.favorite_full);
            binding.favoriteImage.setColorFilter(R.color.green_500);
        }

        if(!star){
            binding.starImage.setImageResource(R.drawable.star_border);
            binding.starImage.setColorFilter(R.color.green_500);
        }
        else {
            binding.favoriteImage.setImageResource(R.drawable.star_full);
            binding.favoriteImage.setColorFilter(R.color.green_500);
        }

        ImageUtils imageUtils = new ImageUtils();

        if(transmitClass.equals("ArticleList")){
            imageUtils.SetImageByByte(binding.articlePicture, ArticleAdapter.articleItemList.get(position).ArticlePicture,R.drawable.chat_ai);
            imageUtils.SetImageByByte(binding.authorFacePicture,ArticleAdapter.articleItemList.get(position).AuthorPicture,R.drawable.chat_ai );
            binding.Title.setText(ArticleAdapter.articleItemList.get(position).Title);
            binding.authorName.setText(ArticleAdapter.articleItemList.get(position).UserName);
            binding.time.setText(ArticleAdapter.articleItemList.get(position).Time);
            binding.content.setText(ArticleAdapter.articleItemList.get(position).Content);
        }
        else {
            imageUtils.SetImageByByte(binding.articlePicture, HomeFragment.cardItems.get(position).ArticlePicture[cardId],R.drawable.chat_ai);
            imageUtils.SetImageByByte(binding.authorFacePicture,HomeFragment.cardItems.get(position).AuthorPicture[cardId],R.drawable.chat_ai );
            binding.Title.setText(HomeFragment.cardItems.get(position).Title[cardId]);
            binding.authorName.setText(HomeFragment.cardItems.get(position).UserName[cardId]);
            binding.time.setText(HomeFragment.cardItems.get(position).Time[cardId]);
            binding.content.setText(HomeFragment.cardItems.get(position).Content[cardId]);
        }


        binding.progressBar.setVisibility(View.GONE);
    }

    //------------------------------------onBackPressed---------------------------------------------------
    private int position;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //------------------------------------Interface---------------------------------------------------

    public ActivityBackFragment activityBackFragment;
    private void returnFragment(){
        Intent returnIntent = new Intent();
        long clickOutTime = System.currentTimeMillis();
        long viewTime = clickOutTime - clickInTime;
        returnIntent.putExtra("cardId",this.cardId);
        returnIntent.putExtra("position", position);
        returnIntent.putExtra("viewTime", viewTime);
        activityBackFragment.intentReturned(returnIntent);
    }

    //------------------------------------OnPause---------------------------------------------------

    @Override
    protected void onPause() {
        super.onPause();
        returnFragment();
    }

    //------------------------------------setClick---------------------------------------------------

    private void setClickListener(){
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}