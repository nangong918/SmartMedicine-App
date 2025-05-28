package com.czy.dal.vo.viewModelVo.post;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.vo.entity.home.CommentVo;

import java.util.ArrayList;
import java.util.List;

public class PostActivityVo {
    public PostVoLd postVoLd = new PostVoLd();
    public List<CommentVo> commentVos = new ArrayList<>();
    public MutableLiveData<Integer> commentNumLd = new MutableLiveData<>(0);
}
