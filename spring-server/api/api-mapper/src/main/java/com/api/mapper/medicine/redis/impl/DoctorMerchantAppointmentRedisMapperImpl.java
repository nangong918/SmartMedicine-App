package com.api.mapper.medicine.redis.impl;

import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.constant.medicine.MedicineRedisKey;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.exception.MedicineExceptions;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 13225
 * @date 2025/9/1 18:05
 * 商户的信息mapper
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DoctorMerchantAppointmentRedisMapperImpl implements DoctorMerchantAppointmentRedisMapper {

    private final RedissonClient redissonClient;

    /// DoctorMerchantAppointmentDo 商户的信息mapper

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

    @Override
    public boolean initAppointmentListSemaphorePermits(@NotNull List<DoctorMerchantAppointmentDo> appointmentDos){
        boolean result = true;
        for (DoctorMerchantAppointmentDo item : appointmentDos) {
            boolean currentResult = initAppointmentSemaphorePermits(item.getId(), item.getRemainCount());
            if (!currentResult) {
                result = false;
            }
        }
        return result; // 返回整体初始化结果
    }

    // 初始化限流桶
    @Override
    public boolean initAppointmentSemaphorePermits(@NotNull Long doctorMerchantAppointmentId, int permitsCount) {
        String semaphoreKeyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_SEMAPHORE_KEY_PREFIX +
                doctorMerchantAppointmentId;

        // 获取信号量
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKeyBuilder);

        // 尝试设置许可数量
        return semaphore.trySetPermits(permitsCount);
    }

    // 预约: 此预约是尝试获取信号量并更新缓存的库存
    @Override
    public boolean reserveAppointment(@NotNull Long doctorMerchantAppointmentId) throws AppException {
        String semaphoreKeyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_SEMAPHORE_KEY_PREFIX +
                doctorMerchantAppointmentId;

        // 获取信号量
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKeyBuilder);

        // 尝试获取信号量
        boolean acquired = semaphore.tryAcquire();

        if (acquired){
            int remainingPermits = semaphore.availablePermits();
            DoctorMerchantAppointmentDo doctorMerchantAppointmentDo = getDoctorMerchantAppointmentDo(doctorMerchantAppointmentId);
            if (doctorMerchantAppointmentDo == null || doctorMerchantAppointmentDo.getId() == null){
                log.warn("[商户: {}不存在]", doctorMerchantAppointmentId);
                throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
            }
            doctorMerchantAppointmentDo.setRemainCount(remainingPermits);
            // 更新
            saveDoctorMerchantAppointmentDo(doctorMerchantAppointmentDo);
            return true;
        }
        else {
            log.warn("[预约失败][信号量获取失败]");
            // 预约失败
            return false;
        }
    }

    // 取消预约: 归还信号量并更新缓存的库存
    @Override
    public boolean cancelAppointment(@NotNull Long doctorMerchantAppointmentId) throws AppException{
        String semaphoreKeyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_SEMAPHORE_KEY_PREFIX +
                doctorMerchantAppointmentId;

        // 获取信号量
        RSemaphore semaphore = redissonClient.getSemaphore(semaphoreKeyBuilder);

        // 归还信号量
        semaphore.release();

        // 获取当前的预约信息
        DoctorMerchantAppointmentDo doctorMerchantAppointmentDo = getDoctorMerchantAppointmentDo(doctorMerchantAppointmentId);

        if (doctorMerchantAppointmentDo == null || doctorMerchantAppointmentDo.getId() == null) {
            log.warn("[商户: {}不存在]", doctorMerchantAppointmentId);
            throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
        }

        // 更新库存
        int currentRemainingCount = semaphore.availablePermits();
        doctorMerchantAppointmentDo.setRemainCount(currentRemainingCount);
        // 更新 Redis 中的预约信息
        saveDoctorMerchantAppointmentDo(doctorMerchantAppointmentDo);

        log.info("[预约已取消][信号量已归还][当前剩余库存: {}]", doctorMerchantAppointmentDo.getRemainCount());
        return true;
    }
}
