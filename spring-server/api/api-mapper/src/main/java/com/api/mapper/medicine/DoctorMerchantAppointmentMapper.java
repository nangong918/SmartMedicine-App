package com.api.mapper.medicine;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.LocationAo;
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

    /// 增加
    void insertDoctorMerchantAppointment(DoctorMerchantAppointmentDo doctorMerchantAppointmentDo);

    void insertDoctorMerchantAppointmentBatch(
            @Param("list") List<DoctorMerchantAppointmentDo> list
    );

    /// 删除
    void deleteDoctorMerchantAppointment(Long id);

    void deleteDoctorMerchantAppointments(@Param("list") List<Long> ids);

    /// 修改
    void updateDoctorMerchantAppointment(DoctorMerchantAppointmentDo item);

    void updateDoctorMerchantAppointments(@Param("list") List<DoctorMerchantAppointmentDo> list);

    /// 查询

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
