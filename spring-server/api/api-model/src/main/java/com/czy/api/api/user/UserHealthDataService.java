package com.czy.api.api.user;

import com.czy.api.domain.Do.user.UserHealthDataDo;

/**
 * @author 13225
 * @date 2025/5/14 16:42
 */
public interface UserHealthDataService {
    void insert(UserHealthDataDo userHealthDataDo);

    void update(UserHealthDataDo userHealthDataDo);

    void deleteByUserId(Long userId);

    UserHealthDataDo findByUserId(Long userId);
}
