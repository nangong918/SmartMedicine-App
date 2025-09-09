package com.czy.domain.dto.http.request;


import com.czy.domain.ao.medicine.AppointmentDoctorSelectAo;

/**
 * @author 13225
 * @date 2025/8/18 14:21
{
    "requestAo":{
        "registerLocation":{
            "province":"广东省",
            "city":"深圳市",
            "region":"南山区"
        },
        "registerTime":"2025-8-21 10:00:00",
        "registerDepartmentCode":1,
        "registerSubjectCode":1,
        "longitude":22.6170,
        "latitude":114.03832
    }
}
 */
public class GetRegisterAppointmentListRequest {
    public AppointmentDoctorSelectAo requestAo;
}
