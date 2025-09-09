package com.czy.domain.vo.entity.home;


import androidx.annotation.NonNull;

import com.czy.baseutil.algorithm.SortItem;
import com.czy.domain.constant.home.PostOperation;
import com.czy.domain.constant.home.RecommendButtonType;
import com.czy.domain.vo.entity.home.post.PostContentEntity;

import java.io.Serializable;
import java.util.List;


public class PostVo extends SortItem implements Serializable, Cloneable {

    // postId
    public Long postId = null;

    // author
    public String authorAvatarUrl;
    public String authorName;

    // post
    public String postTitle;
    // 向前兼容
    public List<PostContentEntity> postContents;
    public String postContent;
    public String postPublishTime;
    // 阅读数量（点击数量）
    public String postViewNum = "0";
    // 点赞数量
    public String likeNum = "0";
    // 收藏数量
    public String collectNum = "0";
    // 评论数量
    public String commentNum = "0";
    // 转发数量
    public String forwardNum = "0";
    public List<String> postImgUrls;

    // action
    public Boolean like;
    public Boolean collect;
    public Boolean dislike;

    // 排序 (时间， 热度， 推荐评分)
    public Long timestamp;
    public Long popularity;
    public Long score;

    /**
     * 点击按钮行为
     * @param recommendButtonType   按钮类型
     * @return  按钮行为
     */
    public PostOperation clickChange(RecommendButtonType recommendButtonType){
        if (recommendButtonType == null || recommendButtonType == RecommendButtonType.NULL){
            return PostOperation.NULL;
        }
        PostOperation operation = PostOperation.NULL;
        switch (recommendButtonType){
            case LIKE -> {
                if (like){
                    // 取消原先状态
                    operation = PostOperation.CANCEL_LIKE;
                }
                else {
                    operation = PostOperation.LIKE;
                }
            }
            case COLLECT -> {
                if (collect){
                    // 取消原先状态
                    operation = PostOperation.CANCEL_COLLECT;
                }
                else {
                    operation = PostOperation.COLLECT;
                }
            }
            case DISLIKE -> {
                if (dislike){
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

    /**
     * 点击按钮行为
     * @param postOperation 按钮行为
     */
    public void clickChange(PostOperation postOperation){
        if (postOperation == null || postOperation == PostOperation.NULL){
            return;
        }
        switch (postOperation){
            case LIKE -> {
                like = true;
                dislike = false;
            }
            case CANCEL_LIKE -> like = false;
            case COLLECT -> collect = true;
            case CANCEL_COLLECT -> collect = false;
            case NOT_INTERESTED -> {
                dislike = true;
                like = false;
            }
            case CANCEL_NOT_INTERESTED -> dislike = false;
        }
    }

    @NonNull
    @Override
    public PostVo clone() throws CloneNotSupportedException {
        return (PostVo) super.clone();
    }
}
