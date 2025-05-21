package com.czy.api.api.feature;

import com.czy.api.domain.ao.feature.UserHeatAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 11:04
 */
public interface UserHeatService {

    /**
     * 获取用户的活跃度
     * @param userId            用户id
     * @return                  用户活跃度
     */
    UserHeatAo getUserHeat(Long userId);

    /**
     * 获取用户列表的活跃度
     * @return                       用户列表的活跃度
     */
    List<UserHeatAo> getUsersHeat();

}
