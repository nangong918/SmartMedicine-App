package com.czy.dal.vo.fragmentActivity;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.constant.Constants;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.ArrayList;
import java.util.List;

public class UserBriefVo {
    // view
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> userAccount = new MutableLiveData<>();
    public MutableLiveData<String> avatarUrl = new MutableLiveData<>();
    public MutableLiveData<String> userRemark = new MutableLiveData<>(null);

    public List<PostVo> userPosts = new ArrayList<>();

    // data
    public Long avatarFileId;
    // 被访问user的Id
    public Long userId = Constants.ERROR_ID;
}
