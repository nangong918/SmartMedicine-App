package com.example.chattest.Home;

import com.example.chattest.User.User;

public class BackArticleBehavior {
    public int userId;
    public int articleId;
    public long viewTime;
    public int like;
    public BackArticleBehavior(){
        this.viewTime = 0;
        this.like = 0;
    }
    public static final int COLLECT = 2;
    public static final int LIKE = 1;
    public static final int UNLIKE = -1;
}
