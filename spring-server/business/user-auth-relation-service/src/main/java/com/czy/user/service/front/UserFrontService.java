package com.czy.user.service.front;

import com.czy.api.domain.vo.user.UserVo;

/**
 * @author 13225
 * @date 2025/6/10 16:27
 */
public interface UserFrontService {


    // user id -> user vo
    UserVo getUserVoById(Long userId);
}
