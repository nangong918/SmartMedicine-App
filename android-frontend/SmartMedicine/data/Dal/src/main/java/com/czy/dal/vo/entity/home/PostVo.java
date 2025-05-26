package com.czy.dal.vo.entity.home;

import com.czy.dal.ao.home.PostInfoUrlAo;
import com.czy.dal.constant.home.PostOperation;
import com.czy.dal.constant.home.RecommendButtonType;

public class PostVo {

    // postId
    public Long postId = null;

    // 文章图片
    public String postImgUrl = "";

    // 文章标题
    public String postTitle = "";

    // 作者名称
    public String authorName = "";

    // 作者头像
    public String authorAvatarUrl = "";

    // 点赞数量
    public Integer likeNum = 0;
    // 收藏数量
    public Integer collectNum = 0;
    // 评论数量
    public Integer commentNum = 0;
    // 阅读数量（点击数量）
    public Integer readNum = 0;
    // 转发数量
    public Integer forwardNum = 0;
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
        postVo.postImgUrl = postInfoUrlAo.fileUrl;
        postVo.postTitle = postInfoUrlAo.title;
        postVo.authorName = postInfoUrlAo.authorName;
        postVo.authorAvatarUrl = postInfoUrlAo.authorAvatarUrl;
        postVo.likeNum = postInfoUrlAo.likeCount.intValue();
        postVo.collectNum = postInfoUrlAo.collectCount.intValue();
        postVo.commentNum = postInfoUrlAo.commentCount.intValue();
        postVo.readNum = postInfoUrlAo.readCount.intValue();
        postVo.forwardNum = postInfoUrlAo.forwardCount.intValue();
        postVo.postPublishTimestamp = postInfoUrlAo.releaseTimestamp;
        // 推荐默认为没看过
        postVo.isLike = false;
        postVo.isCollect = false;
        postVo.isDislike = false;
        return postVo;
    }
}
