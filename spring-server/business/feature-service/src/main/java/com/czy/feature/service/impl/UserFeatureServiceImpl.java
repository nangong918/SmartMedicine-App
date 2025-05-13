package com.czy.feature.service.impl;

import com.czy.api.domain.ao.auth.UserTempFeatureAo;
import com.czy.feature.service.UserFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/5/13 11:50
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserFeatureServiceImpl implements UserFeatureService {


    @Override
    public UserTempFeatureAo getUserTempFeature(Long userId) {
        return null;
    }
}
