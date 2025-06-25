package com.czy.dal.dto.http.request;

public class SendSmsInfoRequest extends DeviceInfoRequest {

    public String phone;
    public String type;
    public SendSmsInfoRequest() {
    }

    public SendSmsInfoRequest(String phone, String type) {
        this.phone = phone;
        this.type = type;
    }
}
