package com.czy.dal.dto.http.request;

public class SendSmsRequest extends DeviceRequest{

    public String phone;
    public String type;
    public SendSmsRequest() {
    }

    public SendSmsRequest(String phone, String type) {
        this.phone = phone;
        this.type = type;
    }
}
