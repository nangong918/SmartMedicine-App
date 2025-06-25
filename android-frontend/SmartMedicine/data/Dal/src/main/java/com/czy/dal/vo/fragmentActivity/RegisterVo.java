package com.czy.dal.vo.fragmentActivity;

import androidx.lifecycle.MutableLiveData;

public class RegisterVo {

    public final MutableLiveData<String> account = new MutableLiveData<>("");
    public final MutableLiveData<String> userName = new MutableLiveData<>("");
    public final MutableLiveData<String> phone = new MutableLiveData<>("");
    public final MutableLiveData<String> vcode = new MutableLiveData<>("");
    public final MutableLiveData<String> pwd = new MutableLiveData<>("");
    public final MutableLiveData<String> pwdAgain = new MutableLiveData<>("");

    // 是否输入了合法的手机号
    public final MutableLiveData<Boolean> isPhoneValid = new MutableLiveData<>(false);
    // 验证码是否合法
    public final MutableLiveData<Boolean> isVcodeValid = new MutableLiveData<>(false);
    // 密码是否合法
    public final MutableLiveData<Boolean> isPwdValid = new MutableLiveData<>(false);
    // 密码是否一致
    public final MutableLiveData<Boolean> isPwdAgainConsist = new MutableLiveData<>(false);
    // 验证码倒计时
    public final MutableLiveData<Integer> vcodeCountDown = new MutableLiveData<>(0);
    // 是否可以点击注册按钮
    public final MutableLiveData<Boolean> isConfirmBtnEnable = new MutableLiveData<>(false);

}
