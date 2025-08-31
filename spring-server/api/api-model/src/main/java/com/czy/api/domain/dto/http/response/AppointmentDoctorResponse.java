package com.czy.api.domain.dto.http.response;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/21 10:40
 */
@Data
public class AppointmentDoctorResponse {
    public Long doctorMerchantAppointmentId;
    public Long orderId;

    public AppointmentDoctorResponse() {
    }

    public AppointmentDoctorResponse(Long doctorMerchantAppointmentId, Long orderId) {
        this.doctorMerchantAppointmentId = doctorMerchantAppointmentId;
        this.orderId = orderId;
    }
}
