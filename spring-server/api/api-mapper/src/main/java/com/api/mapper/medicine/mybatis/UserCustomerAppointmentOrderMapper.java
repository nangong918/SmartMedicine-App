package com.api.mapper.medicine.mybatis;

import com.czy.api.domain.Do.medicine.UserCustomerAppointmentDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 17:16
 */
@Mapper
public interface UserCustomerAppointmentOrderMapper {

    /// 增加
    void insert(UserCustomerAppointmentDo userCustomerAppointmentDo);
    void insertBatch(@Param("list") List<UserCustomerAppointmentDo> list);
    /// 删除
    void delete(Long id);
    void deleteBatch(@Param("list") List<Long> ids);
    /// 修改
    void update(UserCustomerAppointmentDo userCustomerAppointmentDo);
    void updateBatch(@Param("list") List<UserCustomerAppointmentDo> list);
    /// 查询
    List<UserCustomerAppointmentDo> getDosByDoctorMerchantAppointmentId(
            Long doctorMerchantAppointmentId
    );
    List<UserCustomerAppointmentDo> getDosByUserId(Long userId);

}
