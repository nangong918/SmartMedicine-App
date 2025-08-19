package com.api.mapper.medicine;

import com.czy.api.domain.Do.medicine.DoctorRegisterAppointmentDo;
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
public interface DoctorRegisterAppointmentMapper {

    // 根据do记录获取 cardVo的bo

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

            drat.remainCount as remainCount,
            drat.cost as cost,
            drat.beginDate as beginDate,
            drat.endDate as endDate,
            drat.status as status
        FROM doctor_register_appointment AS drat
        LEFT JOIN doctor AS dt ON drat.doctorId = dt.id
        LEFT JOIN hospital AS ht ON drat.hospitalId = ht.id
        WHERE drat.doctorId in (item.doctorId)
     */
    List<RegisterAppointmentDoctorCardBo> getDoctorCardBosByDos(
            List<DoctorRegisterAppointmentDo> dos
    );

    /**
     * 根据参数获取do
     * @param registerLocation                  地点
     * @param registerDate                      日期
     * @param registerDepartmentCode            科室
     * @param registerSubjectCode               科目
     * @return  doctorRegisterAppointmentDo
       SELECT *
       FROM doctor_register_appointment AS drat
       LEFT JOIN hospital AS hos ON drat.hospital_id = hos.id
       WHERE hos.province = #{registerLocation.province}
       AND hos.city = #{registerLocation.city}
       AND hos.region = #{registerLocation.region}
       AND drat.department_id = #{registerDepartmentCode}
       AND drat.subject_id = #{registerSubjectCode}
       AND drat.begin_date <= #{registerDate}
       AND drat.end_date >= #{registerDate}
     */
    List<DoctorRegisterAppointmentDo> getDosByParam(
            @Param("location") LocationAo registerLocation,
            @Param("date") LocalDateTime registerDate,
            @Param("departmentCode") Integer registerDepartmentCode,
            @Param("SubjectCode") Integer registerSubjectCode
    );
}
