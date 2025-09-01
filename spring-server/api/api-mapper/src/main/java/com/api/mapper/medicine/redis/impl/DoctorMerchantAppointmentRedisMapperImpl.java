package com.api.mapper.medicine.redis.impl;

import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.constant.medicine.MedicineRedisKey;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 13225
 * @date 2025/9/1 18:05
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DoctorMerchantAppointmentRedisMapperImpl implements DoctorMerchantAppointmentRedisMapper {

    private final RedissonClient redissonClient;

    /// DoctorMerchantAppointmentDo 商户的信息mapper

    @Async
    @Override
    public boolean saveDoctorMerchantAppointmentDo(@NotNull DoctorMerchantAppointmentDo doctorMerchantAppointmentDo) {
        if (doctorMerchantAppointmentDo.getId() == null){
            return false;
        }
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentDo.getId();

        RBucket<DoctorMerchantAppointmentDo> bucket = redissonClient.getBucket(keyBuilder);
        if (!bucket.isExists()){
            bucket.expire(MedicineRedisKey.Appointment.DoctorMerchant_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        bucket.set(doctorMerchantAppointmentDo);
        return true;
    }

    @Async
    @Override
    public void saveDoctorMerchantAppointmentDos(@NotNull List<DoctorMerchantAppointmentDo> appointmentDos) {
        for (DoctorMerchantAppointmentDo item : appointmentDos) {
            String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX + item.getId();
            RBucket<DoctorMerchantAppointmentDo> bucket = redissonClient.getBucket(keyBuilder);
            bucket.set(item, MedicineRedisKey.Appointment.DoctorMerchant_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

    @Override
    public DoctorMerchantAppointmentDo getDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId) {
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentId;

        // 从 Redis 获取对象
        RBucket<DoctorMerchantAppointmentDo> bucket = redissonClient.getBucket(keyBuilder);

        // 检查对象是否存在
        if (bucket.isExists()) {
            return bucket.get(); // 返回存储的对象
        } else {
            return null; // 如果不存在，返回 null 或者可以抛出异常
        }
    }

    @Override
    public boolean deleteDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId) {
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentId;

        // 获取桶
        RBucket<DoctorMerchantAppointmentDo> bucket = redissonClient.getBucket(keyBuilder);

        // 删除对象并返回结果
        return bucket.delete();
    }

}
