package com.czy.domain.fragmentActivityAo.post;

import androidx.lifecycle.MutableLiveData;

import com.czy.domain.vo.entity.home.CommentVo;

import java.util.ArrayList;
import java.util.List;

public class PostActivityVo {
    public PostVoLd postVoLd = new PostVoLd();
    public List<CommentVo> commentVos = new ArrayList<>();
    public MutableLiveData<Integer> commentNumLd = new MutableLiveData<>(0);
}
