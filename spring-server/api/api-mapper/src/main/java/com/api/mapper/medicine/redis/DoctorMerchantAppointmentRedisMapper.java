package com.api.mapper.medicine.redis;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.bo.medicine.AppointmentDoctorMerchantCardBo;
import exception.AppException;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * 商户的信息mapper
 * 存储数据结构:
 * 1. 商户数据库do
 * @see DoctorMerchantAppointmentDo
 * 2. 商户信号量
 * 3. 商户展示于前端的view 的原始bo
 * @see AppointmentDoctorMerchantCardBo
 */
public interface DoctorMerchantAppointmentRedisMapper {

    boolean saveDoctorMerchantAppointmentDo(@NotNull DoctorMerchantAppointmentDo doctorMerchantAppointmentDo);

    @Async
    void saveDoctorMerchantAppointmentDos(@NotNull List<DoctorMerchantAppointmentDo> appointmentDos);

    void clearAllMerchantAppointmentCache();

    DoctorMerchantAppointmentDo getDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);

    boolean deleteDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);

    boolean initAppointmentListSemaphorePermits(@NotNull List<DoctorMerchantAppointmentDo> appointmentDos);

    boolean initAppointmentSemaphorePermits(@NotNull Long doctorMerchantAppointmentId, int permitsCount);

    // 预约: 此预约是尝试获取信号量并更新缓存的库存
    boolean reserveAppointment(@NotNull Long doctorMerchantAppointmentId) throws AppException;

    // 取消预约: 归还信号量并更新缓存的库存
    boolean cancelAppointment(@NotNull Long doctorMerchantAppointmentId) throws AppException;

    AppointmentDoctorMerchantCardBo getDoctorCardBosByDoctorMerchantDoId(@NotNull Long doctorMerchantId);

    List<AppointmentDoctorMerchantCardBo> getDoctorCardBosByDoctorMerchantDos(@NotNull List<DoctorMerchantAppointmentDo> dos);

    void saveRegisterAppointmentDoctorCardBo(@NotNull AppointmentDoctorMerchantCardBo bo,
                                             @NotNull Long doctorMerchantId);

    void saveRegisterAppointmentDoctorCardBos(@NotNull List<AppointmentDoctorMerchantCardBo> bos,
                                              @NotNull List<DoctorMerchantAppointmentDo> dos);
}
