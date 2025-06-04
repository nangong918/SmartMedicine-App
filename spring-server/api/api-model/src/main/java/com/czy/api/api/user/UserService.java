package com.czy.api.api.user;



import com.czy.api.domain.Do.user.UserDo;

import java.util.List;


/**
 * @author 13225
 * @date 2025/2/20 17:32
 */


public interface UserService {
    Integer checkAccountExist(String userAccount);
    Long getIdByAccount(String userAccount);
    UserDo getUserById(Long id);
    List<UserDo> getByUserIds(List<Long> ids);
    List<UserDo> getByUserIdsWithNull(List<Long> ids);
    UserDo getUserByAccount(String userAccount);
    UserDo getUserByPhone(String phone);
    // 重新设置账号的属性[重新设置userName，头像等][调用方需要进行入参非空校验]
    UserDo resetUserInfo(String account, String newUserName, Long newAvatarFileId);
    // List<userAccount> -> List<userId>
    List<Long> getUserIdListByAccountList(List<String> userAccountList);
    // List<userId> -> List<userAccount>
    List<String> getUserAccountListByUserIdList(List<Long> userIdList);

    /**
     * 注册用户
     * @param userName  用户名
     * @param account   账号
     * @param password  密码
     * @param phone     手机号
     * @param fileId    头像文件Id
     * @return          用户Id
     */
    Long registerUser(String userName, String account, String password, String phone, Long fileId);
}
