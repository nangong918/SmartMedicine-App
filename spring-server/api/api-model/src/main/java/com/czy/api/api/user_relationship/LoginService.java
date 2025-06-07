package com.czy.api.api.user_relationship;


import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.dto.http.response.LoginSignResponse;
import com.czy.api.domain.dto.http.request.LoginUserRequest;

/**
 * @author 13225
 * @date 2025/1/2 14:21
 */

public interface LoginService {

    // 账号重置登录密码
    LoginUserRequest resetUserPasswordByAdmin(String account, String password);

    // 密码注册 [ip检查]
    LoginUserRequest registerUser(String userName, String account, String password);

    // 密码登录 [ip检查]
    boolean checkPassword(String account, String password);

    // 登录
    LoginSignResponse loginUser(LoginJwtPayloadAo loginJwtPayloadAo);

    // 重置密码 [ip检查,signToken检查]
    LoginUserRequest resetUserPasswordByUser(String account, String password, String newPassword);

    // permission查询
    Integer getPermission(Integer userId);
}
