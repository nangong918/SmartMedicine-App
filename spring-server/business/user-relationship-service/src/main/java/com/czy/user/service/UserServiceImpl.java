package com.czy.user.service;

import cn.hutool.core.util.IdUtil;
import com.czy.api.api.oss.OssService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.user_relationship.UserConstant;
import com.czy.api.converter.domain.user.UserConverter;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.user.LoginUserDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.user.UserInfoAo;
import com.czy.api.domain.entity.UserViewEntity;
import com.czy.api.domain.vo.user.UserVo;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.user.mapper.es.UserEsMapper;
import com.czy.user.mapper.mysql.user.LoginUserMapper;
import com.czy.user.mapper.mysql.user.UserMapper;
import com.czy.user.service.front.UserFrontService;
import com.czy.user.service.transactional.UserStorageService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/2/20 17:35
 * 1.查询一个通过senderId查询其加的userId列表然后再用userId一个一个查询user属性，
 * 2.senderId查询其加的userId列表，然后将列表传入数据库查询批量得到user属性list
 * 3.senderId直接用左右全连接查询user属性List。
 * 逐个查询: 可能需要数秒（例如，5-15 秒）。
 * 批量查询: 通常在几百毫秒到几秒之间（例如，0.5-3 秒）。
 * 全连接查询: 通常在几十毫秒到几百毫秒之间（例如，0.1-0.5 秒）。
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final LoginUserMapper loginUserMapper;
    private final UserEsMapper userEsMapper;
    private final UserStorageService userStorageService;
    private final UserFeatureRepository userFeatureRepository;
    private final UserFrontService userFrontService;
    private final RedissonService redissonService;
    private final UserConverter userConverter;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;

    @Override
    public Integer checkAccountExist(String userAccount) {
        return userMapper.checkAccountExist(userAccount);
    }

    @Override
    public Integer checkPhoneExist(String phone) {
        return userMapper.checkPhoneExist(phone);
    }

    @Override
    public Long getIdByAccount(String userAccount) {
        Long result = userMapper.getIdByAccount(userAccount);
        return result == null ? 0 : result;
    }

    @Override
    public UserDo getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public boolean checkUserExist(Long userId) {
        UserDo userDo = userMapper.getUserById(userId);
        return userDo != null &&
                userDo.getId() != null &&
                StringUtils.hasText(userDo.getAccount()) &&
                StringUtils.hasText(userDo.getPhone());
    }

    @Override
    public List<UserDo> getByUserIds(List<Long> ids) {
        return userMapper.getUserListByIdList(ids);
    }

    @Override
    public List<UserDo> getByUserIdsWithNull(List<Long> ids) {
        List<UserDo> userDoList = new ArrayList<>();
        for (Long id : ids){
            if (id == null){
                userDoList.add(null);
                continue;
            }
            UserDo userDo = userMapper.getUserById(id);
            if (userDo != null){
                userDoList.add(userDo);
            }
            else {
                userDoList.add(null);
            }
        }
        return userDoList;
    }

    @Override
    public UserDo getUserByAccount(String userAccount) {
        return userMapper.getUserByAccount(userAccount);
    }

    @Override
    public UserDo getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }

    @Transactional
    @Override
    public UserVo resetUserInfo(UserInfoAo userInfoAo) {
        Long userId = userInfoAo.getUserId();
        // mysql
        UserDo userDo = getUserById(userInfoAo.getUserId());
        userDo.setUserName(userInfoAo.getUsername());
        userMapper.updateUserInfo(userDo);
        // elasticsearch
        userEsMapper.save(userDo);
        // neo4j (不涉及userName)
        // 返回更新后的UserVo
        return userFrontService.getUserVoById(userId);
    }

    @Override
    public List<Long> getUserIdListByAccountList(List<String> userAccountList) {
        if (CollectionUtils.isEmpty(userAccountList)){
            return new ArrayList<>();
        }
        List<Long> list = new ArrayList<>();
        for (String userAccount : userAccountList){
            UserDo userDo = getUserByAccount(userAccount);
            if (userDo == null){
                list.add(null);
            }
            else {
                list.add(userDo.getId());
            }
        }
        return list;
    }

    @Override
    public List<String> getUserAccountListByUserIdList(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)){
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (Long userId : userIdList){
            UserDo userDo = getUserById(userId);
            if (userDo == null){
                list.add(null);
            }
            else {
                list.add(userDo.getAccount());
            }
        }
        return list;
    }

    // 名字不合法 限制最大长度为12，最小长度为6
    private static boolean isNameValid(String userName) {
        return StringUtils.hasText(userName) && userName.length() <= 12 && userName.length() >= 6;
    }


    @Override
    public Long importUser(String userName, String account, String password, String phone, Long fileId) {
        long userId = IdUtil.getSnowflakeNextId();
        LoginUserDo loginUserDo = new LoginUserDo();
        loginUserDo.setId(userId);
        loginUserDo.setUserName(userName);
        loginUserDo.setAccount(account);
        loginUserDo.setPassword(password);
        loginUserDo.setPhone(phone);
        loginUserDo.setPermission(1);
        loginUserDo.setRegisterTime(System.currentTimeMillis());
        loginUserDo.setLastOnlineTime(loginUserDo.getRegisterTime());
        loginUserDo.setAvatarFileId(fileId);

        // 存储到mysql
        loginUserMapper.insertLoginUser(loginUserDo);
        log.info("用户注册成功，loginUserDo: {}", loginUserDo.toJsonString());

        // 基本信息 es + mysql
        UserDo userDo = new UserDo();
        userDo.setId(userId);
        userDo.setUserName(userName);
        userDo.setAccount(account);
        userDo.setPhone(phone);
        userDo.setAvatarFileId(fileId);
        userDo.setRegisterTime(loginUserDo.getRegisterTime());
        userDo.setLastOnlineTime(loginUserDo.getRegisterTime());

        // 存储 es
//        userMapper.insertUserInfo(userDo);
        userEsMapper.save(userDo);

        // 存储到neo4j
        UserFeatureNeo4jDo userFeatureNeo4jDo = new UserFeatureNeo4jDo();
        userFeatureNeo4jDo.setUserId(userId);
        userFeatureNeo4jDo.setName(userName);
        userFeatureNeo4jDo.setAccount(account);
        userFeatureRepository.save(userFeatureNeo4jDo);
        return userId;
    }

    @Override
    public List<UserDo> getUserByIds(List<Long> authorIds) {
        if (CollectionUtils.isEmpty(authorIds)){
            return new ArrayList<>();
        }
        return userMapper.getUserListByIdList(authorIds);
    }

    @Override
    public long updateAvatar(String account, String lockPath) {
        long imageId = IdUtil.getSnowflakeNextId();
        UserDo userDo = userMapper.getUserByAccount(account);
        if (userDo == null || userDo.getId() == null){
            String error = String.format("用户不存在，account: %s", account);
            throw new AppException(error);
        }
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                String.valueOf(userDo.getId()),
                lockPath,
                UserConstant.USER_CHANGE_KEY_EXPIRE_TIME
        );

        if (!redissonService.tryLock(redissonClusterLock)){
            throw new AppException("正在修改头像，请勿频繁点击");
        }

        return imageId;
    }

    @Override
    public UserViewEntity getUserViewEntity(Long userId) {
        UserDo userDo = userMapper.getUserById(userId);
        if (userDo == null || userDo.getId() == null){
            return null;
        }
        String avatarUrl = "";
        if (userDo.getAvatarFileId() != null){
            List<Long> fileIds = new ArrayList<>();
            fileIds.add(userDo.getAvatarFileId());
            List<String> fileUrlsByFileIds = ossService.getFileUrlsByFileIds(fileIds);
            avatarUrl = Optional.ofNullable(fileUrlsByFileIds)
                    .filter(list -> !CollectionUtils.isEmpty(list))
                    .map(l -> l.get(0))
                    .orElse("");
        }
        return userConverter.toUserViewEntity(userDo, avatarUrl);
    }
}
