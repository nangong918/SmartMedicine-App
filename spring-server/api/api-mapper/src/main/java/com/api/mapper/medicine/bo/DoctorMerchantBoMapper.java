package com.api.mapper.medicine.bo;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/20 10:20
 */
public interface DoctorMerchantBoMapper {

    // 根据do记录获取 cardVo的bo todo 待测试
    /**
     *
     * @param list   doList
     * @return      boList
    SELECT
    dt.avatarFileId as doctorAvatarFileId,
    dt.name as doctorName,
    dt.title as doctorTitle,

    ht.name as hospitalName,
    ht.level as hospitalLevel,
    ht.province as locationProvince,
    ht.city as locationCity,
    ht.region as locationRegion,
    ht.longitude as longitude,
    ht.latitude as latitude,

    dma.remainCount as remainCount,
    dma.cost as cost,
    dma.beginDate as beginDate,
    dma.endDate as endDate,
    dma.status as status
    FROM doctor_merchant_appointment AS dma
    LEFT JOIN doctor AS dt ON dma.doctorId = dt.id
    LEFT JOIN hospital AS ht ON dma.hospitalId = ht.id
    WHERE dma.doctorId in (item.doctorId)
     */
    List<RegisterAppointmentDoctorCardBo> getDoctorCardBosByDos(
            @Param("list") List<DoctorMerchantAppointmentDo> list
    );

}
