package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/19 14:43
 */
@Data
public class GetAllRegisterAppointmentDateResponse {
    private List<RegisterAppointmentDataVo> dataVos;
}
