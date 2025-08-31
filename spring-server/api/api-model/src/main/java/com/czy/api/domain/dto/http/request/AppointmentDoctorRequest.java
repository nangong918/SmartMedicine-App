package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/19 15:45
 */
@Data
public class AppointmentDoctorRequest {
    // 公开给全部用户的预约医生记录的id
    @NotNull(message = "预约医生记录的id不能为空")
    public Long doctorMerchantAppointmentId;
    // userId
    @NotNull(message = "申请预约的用户id不能为空")
    public Long userId;
}
