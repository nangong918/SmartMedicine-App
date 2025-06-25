package com.czy.dal.vo.fragmentActivity;


import androidx.lifecycle.MutableLiveData;

/**
 * @author 13225
 */
public class SignVo {

    // 是否勾选了隐私协议
    public final MutableLiveData<Boolean> isAgree = new MutableLiveData<>(false);
    // 手机号码
    public final MutableLiveData<String> phone = new MutableLiveData<>("");
    // 是否输入了合法的手机号
    public final MutableLiveData<Boolean> isPhoneValid = new MutableLiveData<>(false);
    // 是否已经注册了
    public final MutableLiveData<Boolean> isRegistered = new MutableLiveData<>(false);
    // 是否显示密码框？
    public final MutableLiveData<Boolean> isShowPwd = new MutableLiveData<>(false);
    // 密码
    public final MutableLiveData<String> pwd = new MutableLiveData<>("");
}
