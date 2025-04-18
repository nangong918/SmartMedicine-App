package com.czy.user.service;

import com.czy.api.api.user.UserSearchService;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.user.mapper.es.UserEsMapper;
import com.czy.user.mapper.mysql.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/15 18:28
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class UserSearchServiceImpl implements UserSearchService {

    private final UserMapper userMapper;
    private final UserEsMapper userEsMapper;

    @Override
    public List<UserDo> searchUserByLikeAccount(String account) {
        return userMapper.fuzzyGetByAccount(account);
    }

    @Override
    public List<UserDo> searchUserByIkName(String userName) {
        return userEsMapper.findByUserNameContaining(userName);
    }
}
