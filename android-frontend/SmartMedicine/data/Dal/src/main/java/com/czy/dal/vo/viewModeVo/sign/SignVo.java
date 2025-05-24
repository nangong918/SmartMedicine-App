package com.czy.dal.vo.viewModeVo.sign;


import androidx.lifecycle.MutableLiveData;

/**
 * @author 13225
 */
public class SignVo {

    // imgvAgree setImageResource 是否同意隐私协议勾选框
    public final MutableLiveData<Integer> imgvAgreeImageResource = new MutableLiveData<>();
    // btvGetCode setText 倒计时
    public final MutableLiveData<String> btvGetCodeText = new MutableLiveData<>();
    // edtvCode setTextColor
    public final MutableLiveData<Integer> edtvCodeTextColor = new MutableLiveData<>();
    // btvGetCode Background 是否可以发送验证码
    public final MutableLiveData<Integer> btvGetCodeBackground = new MutableLiveData<>();
    // btvLogin Background 是否可以登录
    public final MutableLiveData<Integer> btvLoginBackground = new MutableLiveData<>();

    // 登录错误
    // edtvPhone setTextColor
    public final MutableLiveData<Integer> edtvPhoneTextColor = new MutableLiveData<>();
    // vCode setBackgroundColor
    public final MutableLiveData<Integer> vCodeBackgroundColor = new MutableLiveData<>();
    // 未勾选隐私协议
    // tvAgree setTextColor
    public final MutableLiveData<Integer> tvAgreeTextColor = new MutableLiveData<>();
    // tvPrivacy setTextColor
    public final MutableLiveData<Integer> tvPrivacyTextColor = new MutableLiveData<>();

    // edtvPhone
    public final MutableLiveData<String> edtvPhone = new MutableLiveData<>();
    // edtvCode
    public final MutableLiveData<String> edtvCode = new MutableLiveData<>();
}
