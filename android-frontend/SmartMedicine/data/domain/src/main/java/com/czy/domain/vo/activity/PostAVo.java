package com.czy.domain.vo.activity;


import androidx.lifecycle.MutableLiveData;

import com.czy.domain.ao.entity.CommentAo;

import java.util.ArrayList;
import java.util.List;

/**
 * postVo的liveData，要与PostVo同步
 * @see com.czy.domain.vo.entity.home.PostVo
 */
public class PostAVo {

    // author
    public MutableLiveData<String> authorAvatarUrlLd = new MutableLiveData<>("");
    public MutableLiveData<String> authorNameLd = new MutableLiveData<>("");
    // post
    public MutableLiveData<String> postTitleLd = new MutableLiveData<>("");
    public MutableLiveData<String> postContentLd = new MutableLiveData<>("");
    public MutableLiveData<String> postPublishTimeLd = new MutableLiveData<>("");
    public MutableLiveData<String> postViewNumLd = new MutableLiveData<>("");
    // action
    public MutableLiveData<Boolean> likePostLd = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> collectPostLd = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> dislikePostLd = new MutableLiveData<>(false);

    // comment
    public List<CommentAo> commentAos = new ArrayList<>();
}
