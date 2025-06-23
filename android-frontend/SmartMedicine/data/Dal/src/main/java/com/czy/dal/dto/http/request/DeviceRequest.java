package com.czy.dal.dto.http.request;

public class DeviceRequest extends BaseNettyRequest{

    public String deviceName;
    public String imei;

    public DeviceRequest() {
        super();
    }

    public DeviceRequest(Long senderId) {
        super(senderId);
    }
}
