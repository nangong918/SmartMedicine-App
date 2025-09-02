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
    public final MutableLiveData<String> authorAvatarUrlLd = new MutableLiveData<>("");
    public final MutableLiveData<String> authorNameLd = new MutableLiveData<>("");
    // post
    public final MutableLiveData<String> postTitleLd = new MutableLiveData<>("");
    public final MutableLiveData<String> postContentLd = new MutableLiveData<>("");
    public final MutableLiveData<String> postPublishTimeLd = new MutableLiveData<>("");
    public final MutableLiveData<String> postViewNumLd = new MutableLiveData<>("");
    public final List<String> postImgUrls = new ArrayList<>();
    // action
    public final MutableLiveData<Boolean> likePostLd = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> collectPostLd = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> dislikePostLd = new MutableLiveData<>(false);

    // comment
    public final List<CommentAo> commentAos = new ArrayList<>();
}
