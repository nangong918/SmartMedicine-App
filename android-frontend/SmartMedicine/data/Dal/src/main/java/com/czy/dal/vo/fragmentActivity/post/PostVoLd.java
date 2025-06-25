package com.czy.dal.vo.fragmentActivity.post;


import androidx.lifecycle.MutableLiveData;

import com.czy.dal.vo.entity.home.PostVo;

import java.util.List;

/**
 * postVo的liveData，要与PostVo同步
 * @see com.czy.dal.vo.entity.home.PostVo
 */
public class PostVoLd {

    // postId
    public MutableLiveData<Long> postIdLd = new MutableLiveData<>(null);

    // 文章图片
    public MutableLiveData<List<String>> postImgUrlsLd = new MutableLiveData<>(null);

    // 文章标题
    public MutableLiveData<String> postTitleLd = new MutableLiveData<>("");

    // 文章内容
    public MutableLiveData<String> postContentLd = new MutableLiveData<>("");

    // 作者id
    public MutableLiveData<Long> authorIdLd = new MutableLiveData<>(null);

    // 作者名称
    public MutableLiveData<String> authorNameLd = new MutableLiveData<>("");

    // 作者头像
    public MutableLiveData<String> authorAvatarUrlLd = new MutableLiveData<>("");

    // 点赞数量
    public MutableLiveData<String> likeNumLd = new MutableLiveData<>("0");
    // 收藏数量
    public MutableLiveData<String> collectNumLd = new MutableLiveData<>("0");
    // 评论数量
    public MutableLiveData<String> commentNumLd = new MutableLiveData<>("0");
    // 阅读数量（点击数量）
    public MutableLiveData<String> readNumLd = new MutableLiveData<>("0");
    // 转发数量
    public MutableLiveData<String> forwardNumLd = new MutableLiveData<>("0");
    // 发表时间
    public MutableLiveData<Long> postPublishTimeLd = new MutableLiveData<>(0L);

    // 当前用户是否点赞
    public MutableLiveData<Boolean> isLikeLd = new MutableLiveData<>(false);
    // 当前用户是否收藏
    public MutableLiveData<Boolean> isCollectLd = new MutableLiveData<>(false);
    // 当前用户是否不喜欢
    public MutableLiveData<Boolean> isDislikeLd = new MutableLiveData<>(false);

    public void initByPostVo(PostVo postVo){
        this.postIdLd.setValue(postVo.postId);
        this.postImgUrlsLd.setValue(postVo.postImgUrls);
        this.postTitleLd.setValue(postVo.postTitle);
        this.postContentLd.setValue(postVo.postContent);
        this.authorIdLd.setValue(postVo.authorId);
        this.authorNameLd.setValue(postVo.authorName);
        this.authorAvatarUrlLd.setValue(postVo.authorAvatarUrl);
        this.likeNumLd.setValue(postVo.likeNum);
        this.collectNumLd.setValue(postVo.collectNum);
        this.commentNumLd.setValue(postVo.commentNum);
        this.readNumLd.setValue(postVo.readNum);
        this.forwardNumLd.setValue(postVo.forwardNum);
        this.postPublishTimeLd.setValue(postVo.postPublishTimestamp);
        this.postContentLd.setValue(postVo.postContent);
        this.postTitleLd.setValue(postVo.postTitle);
        this.postImgUrlsLd.setValue(postVo.postImgUrls);
        this.isLikeLd.setValue(postVo.isLike);
        this.isCollectLd.setValue(postVo.isCollect);
        this.isDislikeLd.setValue(postVo.isDislike);
    }
}
