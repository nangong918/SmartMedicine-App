package com.czy.domain.dto.http.response;


/**
 * @author 13225
 * @date 2025/8/21 10:40
 */
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
