package com.api.mapper.medicine.aspect;

import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/9/2 16:07
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class DoctorMerchantAppointmentAspect {

    private final DoctorMerchantAppointmentRedisMapper doctorMerchantAppointmentRedisMapper;

    @Around("execution(* com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper.getById(..))")
    public Object getById(ProceedingJoinPoint joinPoint) throws Throwable {

        // 反射获取方法参数
        Object[] args = joinPoint.getArgs();
        Long doctorMerchantId = (Long) args[0];

        DoctorMerchantAppointmentDo doctorMerchantAppointmentDo =
                doctorMerchantAppointmentRedisMapper.getDoctorMerchantAppointmentDo(doctorMerchantId);

        if (doctorMerchantAppointmentDo == null || doctorMerchantAppointmentDo.getId() == null){
            log.info("缓存未命中, 继续执行mybatis查询");
            Object result = joinPoint.proceed();
            if (result == null){
                return null;
            }
            DoctorMerchantAppointmentDo doResult = (DoctorMerchantAppointmentDo) result;
            doctorMerchantAppointmentRedisMapper.saveDoctorMerchantAppointmentDo(doResult);
            return result;
        }
        else {
            log.info("从redis缓存中获取数据");
            return doctorMerchantAppointmentDo;
        }
    }

}
