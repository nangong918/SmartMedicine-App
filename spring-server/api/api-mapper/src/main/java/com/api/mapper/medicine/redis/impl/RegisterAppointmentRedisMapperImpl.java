package com.api.mapper.medicine.redis.impl;

import com.api.mapper.medicine.redis.RegisterAppointmentRedisMapper;
import com.czy.api.constant.medicine.MedicineRedisKey;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.utils.redisson.service.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/21 13:43
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RegisterAppointmentRedisMapperImpl implements RegisterAppointmentRedisMapper {

    private final RedissonService redissonService;

    /**
     * 缓存预约申请信息
     * @param userId                        用户id
     * @param doctorMerchantAppointmentId   医生商户预约id
     * @param orderId                       订单id
     * @param ao                            预约信息
     * @return                              缓存结果
     */
    @Override
    public boolean saveAppointmentDoctorOrderListAo
    (@NotNull Long userId, @NotNull Long doctorMerchantAppointmentId,
     @NotNull Long orderId, @NotNull AppointmentDoctorOrderListAo ao){
        String keyBuilder = MedicineRedisKey.Appointment.appointmentOrder_KEY_PREFIX +
                userId + ":" +
                doctorMerchantAppointmentId + ":" +
                orderId + ":";
        return redissonService.setObjectByJson(
                keyBuilder,
                ao,
                MedicineRedisKey.Appointment.appointmentOrder_EXPIRE_TIME
        );
    }

    // 全数据查询单个
    @Override
    public AppointmentDoctorOrderListAo getAppointmentDoctorOrderListAo(
            @NotNull Long userId,
            @NotNull Long doctorMerchantAppointmentId,
            @NotNull Long orderId
    ){
        String keyBuilder = MedicineRedisKey.Appointment.appointmentOrder_KEY_PREFIX +
                userId + ":" +
                doctorMerchantAppointmentId + ":" +
                orderId + ":";
        return redissonService.getObjectFromJson(
                keyBuilder,
                AppointmentDoctorOrderListAo.class
        );
    }

    @Override
    public boolean saveDoctorMerchantAppointmentDo(@NotNull DoctorMerchantAppointmentDo doctorMerchantAppointmentDo) {
        if (doctorMerchantAppointmentDo.getId() == null){
            return false;
        }
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentDo.getId();
        return redissonService.setObjectByJson(keyBuilder, doctorMerchantAppointmentDo,
                MedicineRedisKey.Appointment.DoctorMerchant_EXPIRE_TIME);
    }

    @Override
    public DoctorMerchantAppointmentDo getDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId) {
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentId;
        return redissonService.getObjectFromJson(keyBuilder, DoctorMerchantAppointmentDo.class);
    }

    @Override
    public boolean deleteDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId) {
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentId;
        return redissonService.deleteObject(keyBuilder);
    }
}
