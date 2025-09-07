package com.czy.post.service.impl;

import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.post.service.recommend.RecommendedRecordsService;
import com.utils.redisson.service.RedissonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/8/6 10:07
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendedRecordsServiceImpl implements RecommendedRecordsService {

    private final RedissonService redissonService;

    /**
     * 保存推荐记录
     * @param userId        用户ID
     * @param postIds       推荐的帖子ID
     */
    @Override
    public void recordRecommendedPosts(@NonNull Long userId, List<Long> postIds) {
        if (CollectionUtils.isEmpty(postIds)){
            return;
        }

        // 浏览过的redisKey
        String key = OfflineRedisConstant.USER_VIEWED_POSTS_PREFIX + userId;

        // list -> set
        // 将 Set<Long> 转换为 Set<Object>
        Set<Object> postIdSet = postIds.stream()
                .map(id -> (Object) id) // 转换为 Object
                .collect(Collectors.toSet());

        // 存储到redis 中
        redissonService.addSet(key, postIdSet, OfflineRedisConstant.USER_VIEWED_POSTS_EXPIRE_TIME);
    }

    /**
     * 清空用户的浏览记录
     * @param userId    用户ID
     */
    @Override
    public void clearUserRecommendedPostRecords(@NonNull Long userId) {
        // 浏览过的redisKey
        String key = OfflineRedisConstant.USER_VIEWED_POSTS_PREFIX + userId;

        redissonService.removeSet(key);
    }

    /**
     * 获取用户浏览记录
     * @param userId    用户ID
     * @return          推荐帖子ids
     */
    @Override
    public Set<Long> getUserRecommendedPostRecords(@NonNull Long userId) {
        // 浏览过的redisKey
        String key = OfflineRedisConstant.USER_VIEWED_POSTS_PREFIX + userId;

        Set<Object> postIdSet = redissonService.getSet(key);

        return postIdSet.stream()
                .map(id -> (Long) id) // 转换为 Long
                .collect(Collectors.toSet());
    }

}
