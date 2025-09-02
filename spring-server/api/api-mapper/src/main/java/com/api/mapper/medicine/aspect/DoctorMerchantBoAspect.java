package com.api.mapper.medicine.aspect;

import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/2 16:26
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class DoctorMerchantBoAspect {

    private final DoctorMerchantAppointmentRedisMapper doctorMerchantAppointmentRedisMapper;

    @Around("execution(* com.api.mapper.medicine.mybatis.bo.DoctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDoId(..))")
    public Object getById(ProceedingJoinPoint joinPoint) throws Throwable{
        // 反射获取方法参数
        Object[] args = joinPoint.getArgs();
        List<DoctorMerchantAppointmentDo> list = (List<DoctorMerchantAppointmentDo>) args[0];

        // 查找缓存
        List<RegisterAppointmentDoctorCardBo> boList = doctorMerchantAppointmentRedisMapper.getDoctorCardBosByDoctorMerchantDos(
                list
        );

        if (boList != null){
            log.info("[缓存命中]");
            return boList;
        }
        log.info("[缓存未命中]");
        Object result = joinPoint.proceed();
        if (result == null){
            return null;
        }
        List<RegisterAppointmentDoctorCardBo> bosResult = (List<RegisterAppointmentDoctorCardBo>) result;
        doctorMerchantAppointmentRedisMapper.saveRegisterAppointmentDoctorCardBos(
                bosResult,
                list
        );
        return bosResult;
    }
}
