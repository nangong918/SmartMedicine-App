package com.czy.imports.service;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/20 10:31
 */
public interface ImportDoctorMerchantAppointmentService {
    void createDoctorsHospital();

    void createDoctorMerchantAppointmentDos(int count);

    List<DoctorMerchantAppointmentDo> generatorDoctorMerchantAppointmentDos(int count);
}
