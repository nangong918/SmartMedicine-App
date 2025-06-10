package com.czy.api.api.user_relationship;


import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.dto.http.response.LoginSignResponse;
import com.czy.api.domain.dto.http.request.LoginUserRequest;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 13225
 * @date 2025/1/2 14:21
 */

public interface LoginService {

    // 账号重置登录密码
    LoginUserRequest resetUserPasswordByAdmin(String account, String password);

    // 密码注册 [ip检查]
    LoginUserRequest registerUser(String userName, String account, String password);

    long registerUserV2(String phone, String userName, String account, String password, boolean isHaveImage, String lockPath);

    @Transactional
    void registerStorageToDatabase(@NonNull LoginUserDo loginUserDo);

    @Transactional
    void updateStorageToDatabase(@NonNull LoginUserDo loginUserDo);

    // 密码登录 [ip检查]
    boolean checkPassword(String account, String password);

    // 登录
    LoginSignResponse loginUser(LoginJwtPayloadAo loginJwtPayloadAo);

    // 重置密码 [ip检查,signToken检查]
    LoginUserRequest resetUserPasswordByUser(String account, String password, String newPassword);

    // 找回密码
    LoginUserRequest findBackUserPassword(String account, String password);

    // permission查询
    Integer getPermission(Integer userId);
}
