package com.czy.dal.vo.fragmentActivity;

import androidx.lifecycle.MutableLiveData;

public class RegisterVo {

    public final MutableLiveData<String> account = new MutableLiveData<>();
    public final MutableLiveData<String> userName = new MutableLiveData<>();
    public final MutableLiveData<String> phone = new MutableLiveData<>();
    public final MutableLiveData<String> vcode = new MutableLiveData<>();
    public final MutableLiveData<String> pwd = new MutableLiveData<>();
    public final MutableLiveData<String> pwdAgain = new MutableLiveData<>();

}
