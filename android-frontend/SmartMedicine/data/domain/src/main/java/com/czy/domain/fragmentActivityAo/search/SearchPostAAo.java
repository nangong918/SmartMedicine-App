package com.czy.domain.fragmentActivityAo.search;

import androidx.lifecycle.MutableLiveData;

import com.czy.domain.vo.entity.home.PostPreviewExVo;

import java.util.ArrayList;
import java.util.List;

public class SearchPostAAo {
    // 搜索结果
    public List<PostPreviewExVo> postPreviewExVoList = new ArrayList<>();
    // 输入框内容
    public final MutableLiveData<String> searchInputLd = new MutableLiveData<>("");
}
