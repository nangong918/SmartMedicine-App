package com.czy.user.mapper.mysql;


import com.czy.api.domain.Do.user.UserDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/1/2 13:51
 */
@Mapper
public interface UserMapper {

    /**
     * 检查账号是否存在
     * @param userAccount   userAccount
     * @return              0:不存在，>1：存在
     */
    Integer checkAccountExist(String userAccount);
    /**
     * 根据账号获取id
     * @param account
     * @return
     */
    Long getIdByAccount(String account);

    /**
     * getUserById
     * @param id    id
     * @return  UserDo
     */
    UserDo getUserById(Long id);

    /**
     * getUserByAccount
     * @param account
     * @return
     */
    UserDo getUserByAccount(String account);

    /**
     * getUserByPhone
     * @param phone phone
     * @return  UserDo
     */
    UserDo getUserByPhone(String phone);

    // 根据账号模糊查询
    List<UserDo> fuzzyGetByAccount(String account);

    // 更新用户信息
    void updateUserInfo(UserDo userDo);

    // 插入系用户信息
    void insertUserInfo(UserDo userDo);

    List<UserDo> getUserListByIdList(List<Long> idList);
}
