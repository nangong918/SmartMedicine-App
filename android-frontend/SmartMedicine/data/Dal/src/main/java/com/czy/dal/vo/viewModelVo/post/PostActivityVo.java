package com.czy.dal.vo.viewModelVo.post;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.vo.entity.home.CommentVo;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.ArrayList;
import java.util.List;

public class PostActivityVo {
    public MutableLiveData<PostVo> postVoLd = new MutableLiveData<>(new PostVo());
    public MutableLiveData<List<CommentVo>> commentVosLd = new MutableLiveData<>(new ArrayList<>());
}
