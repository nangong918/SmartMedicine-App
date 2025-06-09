package com.czy.api.api.user_relationship;



import com.czy.api.domain.Do.user.UserDo;

import java.util.List;


/**
 * @author 13225
 * @date 2025/2/20 17:32
 */


public interface UserService {

    /**
     * 检查账号是否存在
     * @param userAccount   账号
     * @return              0:不存在
     */
    Integer checkAccountExist(String userAccount);

    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return      0:不存在
     */
    Integer checkPhoneExist(String phone);
    /**
     * 根据账号获取id
     * @param userAccount   账号
     * @return              id
     */
    Long getIdByAccount(String userAccount);
    /**
     * 根据id获取用户
     * @param id    id
     * @return      用户
     */
    UserDo getUserById(Long id);
    /**
     * 根据id获取用户
     * @param ids   id列表
     * @return      用户列表
     */
    List<UserDo> getByUserIds(List<Long> ids);
    /**
     * 根据id获取用户
     * @param ids   id列表
     * @return      用户列表
     */
    List<UserDo> getByUserIdsWithNull(List<Long> ids);
    /**
     * 根据账号获取用户
     * @param userAccount   账号
     * @return              用户
     */
    UserDo getUserByAccount(String userAccount);
    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return      用户
     */
    UserDo getUserByPhone(String phone);
    // 重新设置账号的属性[重新设置userName，头像等][调用方需要进行入参非空校验]
    UserDo resetUserInfo(String account, String newUserName, Long newAvatarFileId);
    // List<userAccount> -> List<userId>
    List<Long> getUserIdListByAccountList(List<String> userAccountList);
    // List<userId> -> List<userAccount>
    List<String> getUserAccountListByUserIdList(List<Long> userIdList);

    /**
     * 导入用户
     * 与LoginService的registerUser不同，因为那里是不涉及file的，这里需要fileId
     * @param userName  用户名
     * @param account   账号
     * @param password  密码
     * @param phone     手机号
     * @param fileId    头像文件Id
     * @return          用户Id
     */
    Long importUser(String userName, String account, String password, String phone, Long fileId);

    /**
     * 获取用户ByIds
     * @param authorIds  作者Id列表
     * @return           用户列表
     */
    List<UserDo> getUserByIds(List<Long> authorIds);
}
