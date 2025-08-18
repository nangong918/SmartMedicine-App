package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.ao.medicine.RegisterAppointmentSelectAo;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/18 14:21
 */
@Data
public class GetRegisterAppointmentListRequest {
    @Valid
    @NotNull(message = "请求参数不能为空")
    public RegisterAppointmentSelectAo requestAo;
}
