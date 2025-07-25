package com.czy.dal.vo.fragmentActivity.search;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.ao.home.PostAo;

import java.util.ArrayList;
import java.util.List;

public class SearchPostVo {
    // 搜索结果
    public List<PostAo> postAoList = new ArrayList<>();
    // 输入框内容
    public final MutableLiveData<String> edtvInputData = new MutableLiveData<>();
}
