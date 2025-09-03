package com.api.mapper.medicine.aspect;

import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.bo.medicine.AppointmentDoctorMerchantCardBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 商户的信息切面
 * 功能: 查询数据库之前先查询Redis缓存
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class DoctorMerchantAppointmentAspect {

    private final DoctorMerchantAppointmentRedisMapper doctorMerchantAppointmentRedisMapper;

    /**
     * 查询 医生商户Merchant记录 缓存
     * @see DoctorMerchantAppointmentDo
     * @param joinPoint     切点
     * @return              DoctorMerchantAppointmentDo
     * @throws Throwable    抛出异常
     */
    @Around("execution(* com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper.getById(..))")
    public Object getById(ProceedingJoinPoint joinPoint) throws Throwable {

        // 反射获取方法参数
        Object[] args = joinPoint.getArgs();
        Long doctorMerchantId = (Long) args[0];

        DoctorMerchantAppointmentDo doctorMerchantAppointmentDo =
                doctorMerchantAppointmentRedisMapper.getDoctorMerchantAppointmentDo(doctorMerchantId);

        if (doctorMerchantAppointmentDo == null || doctorMerchantAppointmentDo.getId() == null){
            log.info("[缓存未命中][DoctorMerchantAppointmentMapper.getById], 继续执行mybatis查询");
            Object result = joinPoint.proceed();
            if (result == null){
                return null;
            }
            DoctorMerchantAppointmentDo doResult = (DoctorMerchantAppointmentDo) result;
            doctorMerchantAppointmentRedisMapper.saveDoctorMerchantAppointmentDo(doResult);
            return result;
        }
        else {
            log.info("[缓存命中][DoctorMerchantAppointmentMapper.getById][doResult: {}]", doctorMerchantAppointmentDo);
            return doctorMerchantAppointmentDo;
        }
    }

    /**
     * 获取商户预约列表cardVo的元数据 AppointmentDoctorMerchantCardBo
     * @see AppointmentDoctorMerchantCardBo
     * @param joinPoint     切点
     * @return              List<AppointmentDoctorMerchantCardBo>
     * @throws Throwable    抛出异常
     */
    @Around("execution(* com.api.mapper.medicine.mybatis.bo.DoctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDos(..))")
    public Object getDoctorCardBosByDoctorMerchantDoId(ProceedingJoinPoint joinPoint) throws Throwable{
        // 反射获取方法参数
        Object[] args = joinPoint.getArgs();
        List<DoctorMerchantAppointmentDo> list = (List<DoctorMerchantAppointmentDo>) args[0];

        // 查找缓存
        List<AppointmentDoctorMerchantCardBo> boList = doctorMerchantAppointmentRedisMapper.getDoctorCardBosByDoctorMerchantDos(
                list
        );

        // 检查缓存内容是否有效，至少有一个元素不为 null
        if (boList != null && !boList.isEmpty() && boList.stream().anyMatch(Objects::nonNull)){
            log.info("[缓存命中][DoctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDos][boList: {}]", boList);
            return boList;
        }
        log.info("[缓存未命中][DoctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDos]");
        Object result = joinPoint.proceed();
        if (result == null){
            return null;
        }
        List<AppointmentDoctorMerchantCardBo> bosResult = (List<AppointmentDoctorMerchantCardBo>) result;
        doctorMerchantAppointmentRedisMapper.saveRegisterAppointmentDoctorCardBos(
                bosResult,
                list
        );
        return bosResult;
    }

}
