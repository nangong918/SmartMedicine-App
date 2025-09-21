package com.czy.domain.dto.http.request;


import com.czy.domain.constant.medicine.AppointmentSortTypeEnum;

/**
 * @author 13225
 * @date 2025/8/19 16:03
 */
public class GetUserAppointmentRecordRequest {
    public Long userId;
    // 排序方式
    public Integer sortType = AppointmentSortTypeEnum.DEFAULT.getCode();
    // 当前经纬度
    public Double userLongitude;
    public Double userLatitude;
}
