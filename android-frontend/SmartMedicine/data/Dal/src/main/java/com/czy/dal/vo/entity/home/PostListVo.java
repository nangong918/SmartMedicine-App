package com.czy.dal.vo.entity.home;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.ao.home.PostAo;

import java.util.ArrayList;
import java.util.List;

public class PostListVo {

    // RecyclerView的Vo LiveData
    public final MutableLiveData<List<PostAo>> postAoListLd = new MutableLiveData<>(new ArrayList<>());

}
