package com.czy.dal.dto.http.request;

public class PhoneLoginInfoRequest extends DeviceInfoRequest {

    public String code;
    public String phone;

    public PhoneLoginInfoRequest() {
    }

    public PhoneLoginInfoRequest(String phone, String code) {
        // 手机号登录的时候没有userId
//        super(phone);
//        Log.d("Intercept", "2BaseNettyRequest: senderId: " + senderId);
//        super.senderId = phone;
        this.code = code;
        this.phone = phone;
    }
}
