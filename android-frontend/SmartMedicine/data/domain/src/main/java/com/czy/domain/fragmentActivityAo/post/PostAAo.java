package com.czy.domain.fragmentActivityAo.post;

import androidx.lifecycle.MutableLiveData;

import com.czy.domain.vo.activity.PostAVo;
import com.czy.domain.vo.entity.home.CommentVo;

import java.util.ArrayList;
import java.util.List;

public class PostAAo {
    // view
    public PostAVo postAVo = new PostAVo();
    public List<CommentVo> commentVos = new ArrayList<>();
    public MutableLiveData<Integer> commentNumLd = new MutableLiveData<>(0);

    // data
    public Long postId;
}
