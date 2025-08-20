package com.api.mapper.medicine;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 17:16
 */
@Mapper
public interface DoctorMerchantAppointmentMapper {

    // 根据do记录获取 cardVo的bo todo 待测试

    /**
     *
     * @param dos   doList
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
            List<DoctorMerchantAppointmentDo> dos
    );

    //  todo 待测试
    /**
     * 根据参数获取do
     * @param registerLocation                  地点
     * @param registerDate                      日期
     * @param registerDepartmentCode            科室
     * @param registerSubjectCode               科目
     * @return  doctorRegisterAppointmentDo
       SELECT *
       FROM doctor_merchant_appointment AS dma
       LEFT JOIN hospital AS hos ON dma.hospital_id = hos.id
       WHERE hos.province = #{registerLocation.province}
       AND hos.city = #{registerLocation.city}
       AND hos.region = #{registerLocation.region}
       AND dma.department_id = #{registerDepartmentCode}
       AND dma.subject_id = #{registerSubjectCode}
       AND dma.begin_date <= #{registerDate}
       AND dma.end_date >= #{registerDate}
     */
    List<DoctorMerchantAppointmentDo> getDosByParam(
            @Param("location") LocationAo registerLocation,
            @Param("date") LocalDateTime registerDate,
            @Param("departmentCode") Integer registerDepartmentCode,
            @Param("SubjectCode") Integer registerSubjectCode
    );
}
