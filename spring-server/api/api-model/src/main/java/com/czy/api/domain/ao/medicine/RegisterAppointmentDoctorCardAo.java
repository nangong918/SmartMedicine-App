package com.czy.api.domain.ao.medicine;

import com.czy.api.domain.vo.medicine.AppointmentDoctorMerchantCardVo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:40
 */
@Data
public class RegisterAppointmentDoctorCardAo {
    private AppointmentDoctorMerchantCardVo vo;
    private String doctorMerchantAppointmentId;
}
