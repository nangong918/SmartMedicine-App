package com.czy.dal.vo.entity.home;


import android.annotation.SuppressLint;

import com.czy.dal.ao.home.PostInfoUrlAo;
import com.czy.dal.constant.home.PostOperation;
import com.czy.dal.constant.home.RecommendButtonType;

import java.util.ArrayList;
import java.util.List;


public class PostVo {

    // postId
    public Long postId = null;

    // 文章图片
    public List<String> postImgUrls = null;

    // 文章标题
    public String postTitle = "";

    // 文章内容
    public String postContent = "";

    // 作者id
    public Long authorId = null;

    // 作者名称
    public String authorName = "";

    // 作者头像
    public String authorAvatarUrl = "";

    // 点赞数量
    public String likeNum = "0";
    // 收藏数量
    public String collectNum = "0";
    // 评论数量
    public String commentNum = "0";
    // 阅读数量（点击数量）
    public String readNum = "0";
    // 转发数量
    public String forwardNum = "0";
    // 发表时间
    public Long postPublishTimestamp = 0L;

    // 当前用户是否点赞
    public Boolean isLike = false;
    // 当前用户是否收藏
    public Boolean isCollect = false;
    // 当前用户是否不喜欢
    public Boolean isDislike = false;

    public PostOperation clickChange(RecommendButtonType recommendButtonType){
        if (recommendButtonType == null || recommendButtonType == RecommendButtonType.NULL){
            return PostOperation.NULL;
        }
        PostOperation operation = PostOperation.NULL;
        switch (recommendButtonType){
            case LIKE -> {
                if (isLike){
                    // 取消原先状态
                    operation = PostOperation.CANCEL_LIKE;
                }
                else {
                    operation = PostOperation.LIKE;
                }
            }
            case COLLECT -> {
                if (isCollect){
                    // 取消原先状态
                    operation = PostOperation.CANCEL_COLLECT;
                }
                else {
                    operation = PostOperation.COLLECT;
                }
            }
            case DISLIKE -> {
                if (isDislike){
                    // 取消原先状态
                    operation = PostOperation.CANCEL_NOT_INTERESTED;
                }
                else {
                    operation = PostOperation.NOT_INTERESTED;
                }
            }
        }
        return operation;
    }

    public void clickChange(PostOperation postOperation){
        if (postOperation == null || postOperation == PostOperation.NULL){
            return;
        }
        switch (postOperation){
            case LIKE -> {
                isLike = true;
                isDislike = false;
            }
            case CANCEL_LIKE -> isLike = false;
            case COLLECT -> isCollect = true;
            case CANCEL_COLLECT -> isCollect = false;
            case NOT_INTERESTED -> {
                isDislike = true;
                isLike = false;
            }
            case CANCEL_NOT_INTERESTED -> isDislike = false;
        }
    }

    public static PostVo getRecommendPostVoFromPostInfoUrlAo(PostInfoUrlAo postInfoUrlAo){
        PostVo postVo = new PostVo();
        postVo.postId = postInfoUrlAo.id;
        postVo.postImgUrls = new ArrayList<>();
        postVo.postImgUrls.add(postInfoUrlAo.fileUrl);
        postVo.postTitle = postInfoUrlAo.title;
        postVo.authorName = postInfoUrlAo.authorName;
        postVo.authorAvatarUrl = postInfoUrlAo.authorAvatarUrl;
        postVo.likeNum = numToString(postInfoUrlAo.likeCount);
        postVo.collectNum = numToString(postInfoUrlAo.collectCount);
        postVo.commentNum = numToString(postInfoUrlAo.commentCount);
        postVo.readNum = numToString(postInfoUrlAo.readCount);
        postVo.forwardNum = numToString(postInfoUrlAo.forwardCount);
        postVo.postPublishTimestamp = postInfoUrlAo.releaseTimestamp;
        // 推荐默认为没看过
        postVo.isLike = false;
        postVo.isCollect = false;
        postVo.isDislike = false;
        return postVo;
    }

    @SuppressLint("DefaultLocale")
    public static String numToString(Long num) {
        if (num < 0) {
            return "Invalid number";
        }

        // 大于 1000M展示为 1B；比如105643909 -> 1.0B
        if (num >= 1_000_000_000) {
            return String.format("%.1fB", num / 1_000_000_000.0);
        }
        // 大于 1000k展示为 1M；比如105643 -> 1.0M
        else if (num >= 1_000_000) {
            return String.format("%.1fM", num / 1_000_000.0);
        }
        // 大于 1000k展示为 1K；比如1105 -> 1.1K
        else if (num >= 1_000) {
            return String.format("%.1fK", num / 1_000.0);
        } else {
            return num.toString();
        }
    }

    public static void main(String[] args) {
        Long num = 1056439090L;
        System.out.println(numToString(num));
    }
}
