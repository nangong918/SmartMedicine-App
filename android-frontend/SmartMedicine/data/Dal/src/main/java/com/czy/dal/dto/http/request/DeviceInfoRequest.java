package com.czy.dal.dto.http.request;

public class DeviceInfoRequest extends BaseHttpRequest {

    public String uuid;
    public String deviceName;
    public String imei;

    public DeviceInfoRequest() {
        super();
    }

    public DeviceInfoRequest(Long senderId) {
        super(senderId);
    }
}
