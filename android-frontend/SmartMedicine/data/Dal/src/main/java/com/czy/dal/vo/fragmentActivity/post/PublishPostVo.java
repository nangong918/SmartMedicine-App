package com.czy.dal.vo.fragmentActivity.post;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

public class PublishPostVo {

    // postTitle
    public MutableLiveData<String> postTitleLd = new MutableLiveData<>("");

    // postContent
    public MutableLiveData<String> postContentLd = new MutableLiveData<>("");

    // image uri
    public MutableLiveData<Uri> imageUriLd = new MutableLiveData<>(null);

}
