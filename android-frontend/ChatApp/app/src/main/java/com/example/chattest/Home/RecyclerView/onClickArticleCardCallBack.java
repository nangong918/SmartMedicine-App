package com.example.chattest.Home.RecyclerView;

public interface onClickArticleCardCallBack {//用来Adapter和Activity通讯
    void onCardClickListener(int position,int cardType,int cardId,boolean[] feedbackButton);
    void onButtonClickListener(int position,int cardType,int cardId,int buttonType,boolean[] feedbackButton);
}