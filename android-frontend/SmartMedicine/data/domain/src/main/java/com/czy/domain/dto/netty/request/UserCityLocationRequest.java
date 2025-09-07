package com.czy.domain.dto.netty.request;


import com.czy.domain.dto.netty.base.BaseRequestData;

/**
 * @author 13225
 * @date 2025/5/23 17:00
 */

public class UserCityLocationRequest extends BaseRequestData {
    public String cityName;
    // 经度 null able
    public Double longitude;
    // 纬度 null able
    public Double latitude;
}
