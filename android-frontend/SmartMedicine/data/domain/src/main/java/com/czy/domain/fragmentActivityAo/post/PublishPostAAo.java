package com.czy.domain.fragmentActivityAo.post;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PublishPostAAo {

    // postTitle
    public final MutableLiveData<String> postTitleLd = new MutableLiveData<>("");

    // postContent
    public final MutableLiveData<String> postContentLd = new MutableLiveData<>("");

    // image uri
    public List<AtomicReference<Uri>> imageUriArList;
}
