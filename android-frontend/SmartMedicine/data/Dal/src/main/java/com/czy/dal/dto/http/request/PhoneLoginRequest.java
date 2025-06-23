package com.czy.dal.dto.http.request;

import android.util.Log;

public class PhoneLoginRequest extends DeviceRequest{

    public String code;
    public String phone;

    public PhoneLoginRequest() {
    }

    public PhoneLoginRequest(String phone, String code) {
        // 手机号登录的时候没有userId
//        super(phone);
//        Log.d("Intercept", "2BaseNettyRequest: senderId: " + senderId);
//        super.senderId = phone;
        this.code = code;
        this.phone = phone;
    }
}
