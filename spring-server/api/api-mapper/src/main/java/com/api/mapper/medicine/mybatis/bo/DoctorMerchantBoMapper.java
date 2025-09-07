package com.api.mapper.medicine.mybatis.bo;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.Do.medicine.UserCustomerAppointmentDo;
import com.czy.api.domain.bo.medicine.AppointmentDoctorMerchantCardBo;
import com.czy.api.domain.bo.medicine.UserAppointmentOrderBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/20 10:20
 */
public interface DoctorMerchantBoMapper {

    //
    /**
     * 根据do记录获取 cardVo的bo 已测试
     * 此方法被AOP标记:
     * @see com.api.mapper.medicine.aspect.DoctorMerchantAppointmentAspect
     * @param list   doList
     * @return      boList
    SELECT
        dt.avatar_file_id AS doctorAvatarFileId,
        dt.name AS doctorName,
        dt.title AS doctorTitle,

        ht.name AS hospitalName,
        ht.level AS hospitalLevel,
        ht.province AS locationProvince,

        ht.city AS locationCity,
        ht.region AS locationRegion,
        ht.longitude AS longitude,
        ht.latitude AS latitude,

        dma.remain_count AS remainCount,
        dma.cost AS cost,
        dma.begin_date AS beginDate,
        dma.end_date AS endDate,
        dma.id AS doctorMerchantId
    FROM doctor_merchant_appointment AS dma
    INNER JOIN doctor AS dt ON dma.doctor_id = dt.id
    INNER JOIN hospital AS ht ON dma.hospital_id = ht.id
    WHERE dma.id IN (item.id)
     */
    List<AppointmentDoctorMerchantCardBo> getDoctorCardBosByDoctorMerchantDos(
            @Param("list") List<DoctorMerchantAppointmentDo> list
    );

    /**
     * 批量获取医生信息
     * @param list  用户订单ids
     * @return  医生信息
     SELECT
        dt.avatar_file_id as doctorAvatarFileId,
        dt.name as doctorName,
        dt.title as doctorTitle,

        ht.name as hospitalName,
        ht.level as hospitalLevel,
        ht.province as locationProvince,
        ht.city as locationCity,
        ht.region as locationRegion,
        ht.longitude as longitude,
        ht.latitude as latitude,

        dma.remain_count AS remainCount,
        dma.cost AS cost,
        dma.begin_date AS beginDate,
        dma.end_date AS endDate,
        dma.id AS doctorMerchantId
    FROM user_customer_appointment_order AS ucao
    INNER JOIN doctor_merchant_appointment AS dma ON ucao.doctor_merchant_appointment_id = dma.id
    INNER JOIN doctor AS dt ON dma.doctor_id = dt.id
    INNER JOIN hospital AS ht ON dma.hospital_id = ht.id
    WHERE dma.id in (item.doctorMerchantAppointmentId)
     */
    List<UserAppointmentOrderBo> getDoctorCardBosByUserCustomerAppointmentDos(
            @Param("list") List<UserCustomerAppointmentDo> list
    );

}
