package com.api.mapper.medicine.redis;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import exception.AppException;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/1 18:05
 * 商户的信息mapper
 */
public interface DoctorMerchantAppointmentRedisMapper {

    boolean saveDoctorMerchantAppointmentDo(@NotNull DoctorMerchantAppointmentDo doctorMerchantAppointmentDo);

    @Async
    void saveDoctorMerchantAppointmentDos(@NotNull List<DoctorMerchantAppointmentDo> appointmentDos);

    DoctorMerchantAppointmentDo getDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);

    boolean deleteDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);

    boolean initAppointmentListSemaphorePermits(@NotNull List<DoctorMerchantAppointmentDo> appointmentDos);

    boolean initAppointmentSemaphorePermits(@NotNull Long doctorMerchantAppointmentId, int permitsCount);

    // 预约: 此预约是尝试获取信号量并更新缓存的库存
    boolean reserveAppointment(@NotNull Long doctorMerchantAppointmentId) throws AppException;

    // 取消预约: 归还信号量并更新缓存的库存
    boolean cancelAppointment(@NotNull Long doctorMerchantAppointmentId) throws AppException;

    RegisterAppointmentDoctorCardBo getDoctorCardBosByDoctorMerchantDoId(@NotNull Long doctorMerchantId);

    List<RegisterAppointmentDoctorCardBo> getDoctorCardBosByDoctorMerchantDos(@NotNull List<DoctorMerchantAppointmentDo> dos);

    void saveRegisterAppointmentDoctorCardBo(@NotNull RegisterAppointmentDoctorCardBo bo,
                                             @NotNull Long doctorMerchantId);

    void saveRegisterAppointmentDoctorCardBos(@NotNull List<RegisterAppointmentDoctorCardBo> bos,
                                              @NotNull List<DoctorMerchantAppointmentDo> dos);
}
