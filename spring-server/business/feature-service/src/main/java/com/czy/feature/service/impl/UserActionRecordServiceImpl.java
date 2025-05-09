package com.czy.feature.service.impl;

import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.feature.service.UserActionRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 13225
 * @date 2025/5/9 15:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserActionRecordServiceImpl implements UserActionRecordService {
    @Override
    public void uploadUserInfo(UserCityLocationInfoAo ao) {

    }

    @Override
    public void clickPost(Long userId, Long postId, Long clickTimestamp) {

    }

    @Override
    public void uploadClickPostAndBrowseTime(Long userId, Long postId, Long browseTime) {

    }
}
