package com.offline.recommend.service;

import lombok.NonNull;

import java.util.List;
import java.util.Set;

/**
 * @author 13225
 * @date 2025/8/6 10:07
 */
public interface RecommendedRecordsService {
    /**
     * 保存推荐记录
     * @param userId        用户ID
     * @param postIds       推荐的帖子ID
     */
    void recordRecommendedPosts(@NonNull Long userId, List<Long> postIds);
    /**
     * 获取用户最近浏览的帖子
     * @param userId    用户ID
     */
    void clearUserRecommendedPostRecords(@NonNull Long userId);
    /**
     * 获取用户推荐
     * @param userId    用户ID
     * @return          推荐帖子ids
     */
    Set<Long> getUserRecommendedPostRecords(@NonNull Long userId);
}
