package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.ao.medicine.RegisterAppointmentSelectAo;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
@Data
public class GetRegisterAppointmentListRequest {
    @Valid
    @NotNull(message = "请求参数不能为空")
    public RegisterAppointmentSelectAo requestAo;
}
