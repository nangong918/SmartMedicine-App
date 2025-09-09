package com.czy.domain.ao.medicine;


import com.czy.domain.ao.LocationAo;

/**
 * @author 13225
 * @date 2025/8/18 10:34
 * 挂号预约的参数
 */
public class AppointmentDoctorSelectAo {
    public LocationAo registerLocation;
    // 2025-09-06 09:00:00: yyyy-MM-dd HH:mm:ss
    public String registerTime;

    public Integer registerDepartmentCode;
    public Integer registerSubjectCode;
    /// 经纬度
    public Double longitude;
    public Double latitude;
}
