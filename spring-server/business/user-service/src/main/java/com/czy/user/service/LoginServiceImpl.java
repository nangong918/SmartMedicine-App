package com.czy.user.service;


import com.czy.api.api.user.LoginService;
import com.czy.api.api.auth.TokenGeneratorService;
import com.czy.api.constant.user.UserConstant;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.ao.auth.LoginTokenAo;
import com.czy.api.domain.dto.http.response.LoginSignResponse;
import com.czy.api.domain.dto.http.request.LoginUserRequest;
import com.czy.springUtils.util.EncryptUtil;
import com.czy.user.mapper.es.UserEsMapper;
import com.czy.user.mapper.mysql.LoginUserMapper;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * @author 13225
 * @date 2025/1/2 14:25
 * WebSocket心跳连接：[Redis]顶号检查；SignToken过期检查
 */

@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class LoginServiceImpl implements LoginService {

    // Dubbo远程调用Auth服务
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private TokenGeneratorService tokenGeneratorService;
    public static final String defaultPassword = "123456";
    private final LoginUserMapper loginUserMapper;
    private final UserEsMapper userEsMapper;

    @Override
    public LoginUserRequest resetUserPasswordByAdmin(String account, String password) throws AppException {
        LoginUserDo loginUserDO = loginUserMapper.getLoginUserByAccount(account);
        if (loginUserDO != null){
            loginUserDO.setPassword(password == null ? defaultPassword : password);
            loginUserMapper.updateLoginUser(loginUserDO);
            LoginUserRequest newLoginUserRequest = new LoginUserRequest();
            BeanUtils.copyProperties(loginUserDO, newLoginUserRequest);
            return newLoginUserRequest;
        }
        else {
            String errorMsg = String.format("用户account不存在，account: %s", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
    }

    @Override
    public LoginUserRequest registerUser(String userName, String account, String password) throws AppException {
        LoginUserDo loginUserDo = new LoginUserDo();
        loginUserDo.setUserName(userName);
        loginUserDo.setAccount(account);
        loginUserDo.setPassword(StringUtils.hasText(password) ? password : defaultPassword);
        loginUserDo.setRegisterTime(System.currentTimeMillis());
        loginUserDo.setLastOnlineTime(loginUserDo.getRegisterTime());
        // 加密存储
        loginUserDo.setPassword(EncryptUtil.bcryptEncrypt(loginUserDo.getPassword()));
        loginUserDo.setPermission(UserConstant.User_Permission);
        int num = loginUserMapper.insertLoginUser(loginUserDo);
        if (num <= 0){
            String errorMsg = String.format("用户注册失败，account: %s", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        // 注册成功
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        // 此处是反射赋值，可以考虑使用 mapstruct
        BeanUtils.copyProperties(loginUserDo, loginUserRequest);
        // 存储UserName到ElasticSearch
        UserDo userDo = new UserDo();
        BeanUtils.copyProperties(loginUserDo, userDo);
        userDo.setAvatarUrl("");// TODO 头像地址需要设置，后面完成整个系统再更新OSS逻辑
        // 存储到ES
        userEsMapper.save(userDo);
        return loginUserRequest;
    }

    @Override
    public boolean checkPassword(String account, String password) {
        LoginUserDo loginUserDo = checkAccountExist(account);

        if (!EncryptUtil.bcryptVerify(password, loginUserDo.getPassword())){
            String errorMsg = String.format("用户password错误，account: %s", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        return true;
    }

    // 返回accessToken
    @Override
    public LoginSignResponse loginUser(LoginJwtPayloadAo loginJwtPayloadAo) {
        LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount(loginJwtPayloadAo.getUserAccount());
        // 生成accessToken
        LoginSignResponse loginSignResponse = new LoginSignResponse();
        // 告诉前端建立WebSocket连接
        loginSignResponse.setComeConnectWebsocket(true);
        BeanUtils.copyProperties(loginUserDo, loginSignResponse);
        loginSignResponse.setUserId(loginUserDo.getId().toString());
        try {
            String accessToken = tokenGeneratorService.generateAccessToken(loginJwtPayloadAo);
            String refreshToken = tokenGeneratorService.generateRefreshToken(loginJwtPayloadAo);
            // 设置AccessToken和RefreshToken应该在AuthService
            loginSignResponse.setLoginTokenAo(new LoginTokenAo(accessToken, refreshToken));
            loginUserMapper.setLastOnlineTime(loginUserDo.getId(), System.currentTimeMillis());
        } catch (Exception e){
            throw new AppException(e.getMessage());
        }

        return loginSignResponse;
    }

    @Override
    public LoginUserRequest resetUserPasswordByUser(String account, String password, String newPassword) throws AppException{
        if (!password.equals(newPassword)){
            String errorMsg = String.format("用户旧密码和新密码不能相同，account: %s", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        else {
            LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount(account);
            if (loginUserDo != null){
                String encryptedNewPassword = EncryptUtil.bcryptEncrypt(newPassword);
                loginUserDo.setPassword(encryptedNewPassword);
                loginUserMapper.updateLoginUser(loginUserDo);
                LoginUserRequest newLoginUserRequest = new LoginUserRequest();
                BeanUtils.copyProperties(loginUserDo, newLoginUserRequest);
                return newLoginUserRequest;
            }
            else {
                String errorMsg = String.format("用户account不存在，account: %s", account);
                log.warn(errorMsg);
                throw new AppException(errorMsg);
            }
        }
    }

    @Override
    public Integer getPermission(Integer userId) {
        try {
            return (userId != null) ? loginUserMapper.getLoginUser(userId).getPermission() : null;
        } catch (Exception e) {
            return null; // 如果有异常，返回null
        }
    }

    private LoginUserDo checkAccountExist(String account) {
        LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount(account);
        if (loginUserDo == null){
            String errorMsg = String.format("用户account不存在，account: %s", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        return loginUserDo;
    }
}
