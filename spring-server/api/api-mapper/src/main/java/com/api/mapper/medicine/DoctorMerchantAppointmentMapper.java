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

    /**
     * 乐观锁更新库存
     * 乐观锁是先计算后进行原子操作，然后为了避免原子操作的错误，要进行库存跟计算之间的校验
     * @param id                 id
     * @param expectedCount      期望库存
     * @return                   更新结果
     */
    int compareAndDecrement(@Param("id") Long id, @Param("expectedCount") int expectedCount);

    /**
     * 悲观锁更新库存
     * 悲观锁在操作开始时直接加锁，确保其他事务无法访问相关数据。
     * @param id                 id
     * @return                   更新结果
     */
    int decrementWithPessimisticLock(@Param("id") Long id);

    /**
     * 直接减少库存
     * @param id    商品id
     * @return      减库存结果 0:失败 1:成功
     */
    int decrementRemainCount(Long id);

    /// 查询

    //  已测试
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

    // 获取指定id的预约记录
    DoctorMerchantAppointmentDo getById(@Param("id") Long doctorMerchantId);

    // 添加行级锁查询
    DoctorMerchantAppointmentDo getByIdForUpdate(@Param("id") Long id);
}
