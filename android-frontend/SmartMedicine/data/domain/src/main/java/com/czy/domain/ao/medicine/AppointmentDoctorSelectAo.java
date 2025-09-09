package com.czy.domain.ao.medicine;


import com.czy.domain.ao.LocationAo;

/**
 * @author 13225
 * @date 2025/8/18 10:34
 * 挂号预约的参数
 */
public class AppointmentDoctorSelectAo {
    public LocationAo registerLocation;
    // 9月19日：yyyy-MM-dd格式
    public String registerTime;

    public Integer registerDepartmentCode;
    public Integer registerSubjectCode;
    /// 经纬度
    public Double longitude;
    public Double latitude;
}
