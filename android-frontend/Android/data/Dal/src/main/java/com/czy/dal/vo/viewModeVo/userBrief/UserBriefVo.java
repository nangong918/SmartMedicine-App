package com.czy.dal.vo.viewModeVo.userBrief;

import androidx.lifecycle.MutableLiveData;

public class UserBriefVo {
    // view
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> avatarUrl = new MutableLiveData<>();

    // data
    public MutableLiveData<String> userAccount = new MutableLiveData<>();
}
