package com.czy.user.service;

import com.czy.api.api.user.UserService;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.user.mapper.mysql.UserMapper;
import com.czy.user.service.transactional.UserStorageService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
    private final UserStorageService userStorageService;

    @Override
    public Integer checkAccountExist(String userAccount) {
        return userMapper.checkAccountExist(userAccount);
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

    @Override
    public UserDo resetUserInfo(String account, String newUserName, Long newAvatarFileId) {
        if (checkAccountExist(account) <= 0){
            String errorMsg = String.format("用户account不存在，account: %s", account);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        UserDo userDo = getUserByAccount(account);
        if (!isNameValid(newUserName)){
            String errorMsg = String.format("用户名不合法，userName: %s", newUserName);
            log.warn(errorMsg);
            throw new AppException(errorMsg);
        }
        if (userDo != null){
            userDo.setUserName(newUserName);
            if (!ObjectUtils.isEmpty(newAvatarFileId)){
                userDo.setAvatarFileId(newAvatarFileId);
            }
            try {
                userStorageService.saveUserToDataBase(userDo);
            } catch (Exception e) {
                log.error("同步更新失败", e);
                throw new AppException("更新失败", e);
            }
            return userDo;
        }
        throw new AppException("更新失败");
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
}
