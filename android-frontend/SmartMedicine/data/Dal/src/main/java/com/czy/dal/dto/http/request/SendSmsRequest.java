package com.czy.dal.dto.http.request;

public class SendSmsRequest extends DeviceInfoRequest {

    public String phone;
    public String smsType;
    public SendSmsRequest() {
    }

    public SendSmsRequest(String phone, String smsType) {
        this.phone = phone;
        this.smsType = smsType;
    }
}
