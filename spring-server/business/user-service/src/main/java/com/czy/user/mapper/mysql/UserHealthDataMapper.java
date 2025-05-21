package com.czy.user.mapper.mysql;

import com.czy.api.domain.Do.user.UserHealthDataDo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 13225
 * @date 2025/5/14 16:35
 */
@Mapper
public interface UserHealthDataMapper {

    void insert(UserHealthDataDo userHealthDataDo);

    void update(UserHealthDataDo userHealthDataDo);

    void deleteByUserId(Long userId);

    UserHealthDataDo findByUserId(Long userId);

}
