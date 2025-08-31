package com.czy.api.domain.ao.medicine;

import com.czy.api.domain.vo.medicine.RegisterAppointmentDoctorCardVo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:40
 */
@Data
public class RegisterAppointmentDoctorCardAo {
    private RegisterAppointmentDoctorCardVo vo;
    private String doctorMerchantAppointmentId;
}
