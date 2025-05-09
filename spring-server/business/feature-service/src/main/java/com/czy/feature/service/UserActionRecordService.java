package com.czy.feature.service;

import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;

/**
 * @author 13225
 * @date 2025/5/9 15:38
 */
public interface UserActionRecordService {

    // 上传用户的城市等信息
    void uploadUserInfo(UserCityLocationInfoAo ao);

    // 用户点击帖子（与浏览时长拆开，避免用户直接划掉后台）
    void clickPost(Long userId, Long postId, Long clickTimestamp);

    // 上传用用的点击帖子 + 浏览时长
    void uploadClickPostAndBrowseTime(Long userId, Long postId, Long browseTime);
}
