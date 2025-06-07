package com.czy.user.service.transactional;

import com.czy.api.domain.Do.user.UserDo;
import com.czy.user.mapper.es.UserEsMapper;
import com.czy.user.mapper.mysql.user.UserMapper;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 13225
 * @date 2025/4/16 13:58
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserStorageServiceImpl implements UserStorageService{

    private final UserMapper userMapper;
    private final UserEsMapper userEsMapper;

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void saveUserToDataBase(UserDo userDo) throws Exception {
        try {
            // 更新到 MySQL
            userMapper.updateUserInfo(userDo);

            // 更新到 Elasticsearch
            if (userDo.getId() != null) {
                userEsMapper.save(userDo);
            }
        } catch (Exception e) {
            // 处理异常并抛出，使事务回滚
            log.error("更新用户信息失败", e);
            throw new AppException("更新失败", e);
        }
    }
}
