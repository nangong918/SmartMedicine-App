package com.czy.user.service;


import cn.hutool.core.util.IdUtil;
import com.czy.api.api.auth.TokenGeneratorService;
import com.czy.api.api.user_relationship.LoginService;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.converter.domain.user.UserConverter;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.auth.LoginJwtPayloadAo;
import com.czy.api.domain.ao.auth.LoginTokenAo;
import com.czy.api.domain.dto.http.request.LoginUserRequest;
import com.czy.api.domain.dto.http.response.LoginSignResponse;
import com.czy.api.domain.vo.user.UserVo;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.springUtils.util.EncryptUtil;
import com.czy.user.mapper.es.UserEsMapper;
import com.czy.user.mapper.mysql.user.LoginUserMapper;
import com.czy.user.service.front.UserFrontService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import exception.AppException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final RedissonService redissonService;
    private final UserConverter userConverter;
    private final UserFeatureRepository userFeatureRepository;
    private final UserFrontService UserFrontService;

    @Override
    public LoginUserRequest resetUserPasswordByAdmin(String account, String password) throws AppException {
        LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount(account);
        if (loginUserDo != null && loginUserDo.getId() != null){
            loginUserDo.setPassword(password == null ? defaultPassword : password);
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
        userDo.setAvatarFileId(null);// TODO 头像地址需要设置，后面完成整个系统再更新OSS逻辑
        // 存储到ES
        userEsMapper.save(userDo);
        return loginUserRequest;
    }

    @Override
    public long registerUserV2(String phone, String userName, String account, String password, boolean isHaveImage, String lockPath) {
        long userId = IdUtil.getSnowflakeNextId();
        LoginUserDo loginUserDo = new LoginUserDo();
        loginUserDo.setId(userId);
        loginUserDo.setPhone(phone);
        loginUserDo.setUserName(userName);
        loginUserDo.setAccount(account);
        loginUserDo.setPassword(StringUtils.hasText(password) ? password : defaultPassword);
        // 加密存储
        loginUserDo.setPassword(EncryptUtil.bcryptEncrypt(loginUserDo.getPassword()));
        loginUserDo.setRegisterTime(System.currentTimeMillis());
        loginUserDo.setLastOnlineTime(loginUserDo.getRegisterTime());
        loginUserDo.setPermission(UserConstant.User_Permission);

        // 无文件的情况
        if (!isHaveImage){
            registerStorageToDatabase(loginUserDo);
        }
        else {
            // 将数据存储到Redis中，等待oss上传完成之后再执行存储数据库操作，避免分布式事务
            // 1.给用户phone上分布式锁
            // 此时userId还没确定，所以用phone作为唯一key

            RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                    phone,
                    lockPath,
                    UserConstant.USER_CHANGE_KEY_EXPIRE_TIME
            );
            if (!redissonService.tryLock(redissonClusterLock)){
                throw new AppException("用户正在注册，请稍等......");
            }

            // 2.缓存到redis
            try {
                registerUserFirst(loginUserDo);
            } catch (Exception e){
                if (e instanceof AppException){
                    // 交给全局或异常处理
                    throw new AppException(e.getMessage());
                }
                else {
                    log.error("用户注册失败", e);
                }
                // 失败才解除分布式锁，因为此处是防止用户频繁点击的。不能用finally解除
                redissonService.unlock(redissonClusterLock);
            }
        }

        return userId;
    }

    private void registerUserFirst(@NonNull LoginUserDo loginUserDo) throws Exception{
        // 由于两次http请求可能都不是一个服务处理的，所以数据需要缓存在redis
        // redis的存储key是：user_register: + phone
        // key统一格式：user_register:phone
        String key = UserConstant.USER_REGISTER_REDIS_KEY + loginUserDo.getPhone();
        boolean result = redissonService.setObjectByJson(key, loginUserDo, UserConstant.USER_CHANGE_KEY_EXPIRE_TIME);
        if (!result){
            log.warn("用户注册失败，account: {}", loginUserDo.getAccount());
            throw new AppException("用户注册失败");
        }
        else {
            loginUserDo = redissonService.getObjectFromJson(key, LoginUserDo.class);
            log.info("已经将用户信息缓存到Redis，redis-key：{}，存储信息：{}", key, loginUserDo.toJsonString());
        }
    }

    @Transactional
    @Override
    public void registerStorageToDatabase(@NonNull LoginUserDo loginUserDo){
        /*
        直接存储
            1. user_info -> mysql
            2. user_name -> elasticsearch
            3. user_node -> neo4j
         */
        ;
        // user_info -> mysql
        loginUserMapper.insertLoginUser(loginUserDo);

        // user_name -> elasticsearch
        UserDo userDo = userConverter.toUserDo_(loginUserDo);
        userDo.setAvatarFileId(null);
        userDo.setRegisterTime(System.currentTimeMillis());
        userDo.setLastOnlineTime(userDo.getRegisterTime());
        userEsMapper.save(userDo);

        // user_node -> neo4j
        UserFeatureNeo4jDo userFeatureNeo4jDo = userConverter.toUserFeatureNeo4jDo_(loginUserDo);
        userFeatureRepository.save(userFeatureNeo4jDo);
    }

    @Transactional
    @Override
    public void updateStorageToDatabase(@NonNull LoginUserDo loginUserDo){
        // user_info -> mysql
        loginUserMapper.updateLoginUser(loginUserDo);

        // user_name -> elasticsearch
        UserDo userDo = userConverter.toUserDo_(loginUserDo);
        userDo.setAvatarFileId(null);
        userDo.setRegisterTime(System.currentTimeMillis());
        userDo.setLastOnlineTime(userDo.getRegisterTime());
        userEsMapper.save(userDo);
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
        UserVo userVo = UserFrontService.getUserVoById(loginUserDo.getId());
        loginSignResponse.setUserVo(userVo);
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
            return findBackUserPassword(account, newPassword);
        }
    }

    @Override
    public LoginUserRequest findBackUserPassword(String account, String newPassword) {
        LoginUserDo loginUserDo = loginUserMapper.getLoginUserByAccount(account);
        if (loginUserDo != null && loginUserDo.getId() != null){
            String encryptedNewPassword = EncryptUtil.bcryptEncrypt(newPassword);
            loginUserDo.setPassword(encryptedNewPassword);
            log.info("重置密码的userId为：{}", loginUserDo.getId());
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

    @Override
    public Integer getPermission(Long userId) {
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
