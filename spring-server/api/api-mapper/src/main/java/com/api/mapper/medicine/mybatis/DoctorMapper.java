package com.api.mapper.medicine.mybatis;

import com.czy.api.domain.Do.medicine.DoctorDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 17:15
 */
@Mapper
public interface DoctorMapper {

    /// 增
    void insert(DoctorDo doctorDo);

    void insertBatch(@Param("doctorDos") List<DoctorDo> doctorDos);

    /// 删

    /// 改

    /// 查
    DoctorDo getById(Long id);

    List<DoctorDo> getByIds(@Param("ids") List<Long> ids);

    int getCount();

    List<DoctorDo> getRandomByLimit(@Param("limit") int limit);
}
