package com.czy.api.domain.ao.medicine;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/20 17:54
 */
@Data
public class AppointmentDoctorAo {
    private Long userId;
    private Long doctorMerchantAppointmentId;
    private Long orderId;
}
