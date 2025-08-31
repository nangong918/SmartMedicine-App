package com.czy.user.service;

import com.api.mapper.user.mybatis.user.UserHealthDataMapper;
import com.czy.api.api.user.UserHealthDataService;
import com.czy.api.domain.Do.user.UserHealthDataDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/5/14 16:43
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class UserHealthDataServiceImpl implements UserHealthDataService {

    private final UserHealthDataMapper userHealthDataMapper;

    @Override
    public void insert(UserHealthDataDo userHealthDataDo) {
        userHealthDataMapper.insert(userHealthDataDo);
    }

    @Override
    public void update(UserHealthDataDo userHealthDataDo) {
        userHealthDataMapper.update(userHealthDataDo);
    }

    @Override
    public void deleteByUserId(Long userId) {
        userHealthDataMapper.deleteByUserId(userId);
    }

    @Override
    public UserHealthDataDo findByUserId(Long userId) {
        return userHealthDataMapper.findByUserId(userId);
    }
}
