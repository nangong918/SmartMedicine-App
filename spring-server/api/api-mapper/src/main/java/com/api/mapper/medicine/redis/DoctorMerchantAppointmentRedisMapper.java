package com.api.mapper.medicine.redis;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/1 18:05
 */
public interface DoctorMerchantAppointmentRedisMapper {

    @Async
    boolean saveDoctorMerchantAppointmentDo(@NotNull DoctorMerchantAppointmentDo doctorMerchantAppointmentDo);

    @Async
    void saveDoctorMerchantAppointmentDos(@NotNull List<DoctorMerchantAppointmentDo> appointmentDos);

    DoctorMerchantAppointmentDo getDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);

    boolean deleteDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);
}
