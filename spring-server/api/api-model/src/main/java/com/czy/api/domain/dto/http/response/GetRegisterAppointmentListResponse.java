package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/19 14:41
 */
@Data
public class GetRegisterAppointmentListResponse {
    private RegisterAppointmentPageVo pageVo;
}
