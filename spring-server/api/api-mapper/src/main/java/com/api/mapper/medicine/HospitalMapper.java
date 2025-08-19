package com.api.mapper.medicine;

import com.czy.api.domain.Do.medicine.HospitalDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 17:16
 */
@Mapper
public interface HospitalMapper {

    /// 增
    void insert(HospitalDo hospitalDo);

    void insertBatch(List<HospitalDo> hospitalDos);

    /// 删

    /// 改

    /// 查
    HospitalDo getById(Long id);

    List<HospitalDo> getByIds(List<Long> ids);
}
