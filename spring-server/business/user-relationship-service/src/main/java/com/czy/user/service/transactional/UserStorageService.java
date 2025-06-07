package com.czy.user.service.transactional;

import com.czy.api.domain.Do.user.UserDo;

/**
 * @author 13225
 * @date 2025/4/16 13:56
 */
public interface UserStorageService {

    /**
     * 用事务保存用户信息到数据库：MySQL，ES
     * @param userDo        用户信息
     * @throws Exception    抛出异常，事务回滚
     */
    void saveUserToDataBase(UserDo userDo) throws Exception;

}
