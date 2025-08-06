package com.offline.recommend.service.impl;


import com.czy.api.api.offline.OfflineRecommendService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.offline.recommend.service.RecommendedRecordsService;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/5/20 14:08
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class OfflineRecommendServiceImpl implements OfflineRecommendService {

    private final RedissonService redissonService;
    private final RecommendedRecordsService recommendedRecordsService;

    /**
     * 获取离线推荐
     * @param userId    用户ID
     * @return          推荐帖子ids
     */
    @Override
    public List<PostScoreAo> getOfflineRecommend(Long userId) {
        // 获取缓存在redis的推荐帖子id
        String userRecommendKey = OfflineRedisConstant.USER_RECOMMEND_KEY + ":" + userId;

        // 获取离线层计算出来的帖子
        if (redissonService.zCount(userRecommendKey) > 0){
            // redis中取出指定数量的post, 并且从未推荐的post记录中删除
            List<Object> recommendPostScoreAos = redissonService.zPopTopNAndRemove(
                    userRecommendKey, FeatureConstant.USER_RECOMMEND_GET_NUM
            );

            List<PostScoreAo> postScoreAoList = new ArrayList<>();
            if (CollectionUtils.isEmpty(recommendPostScoreAos)){
                return new ArrayList<>();
            }

            for (Object recommendPostScoreAo : recommendPostScoreAos){
                if (recommendPostScoreAo instanceof PostScoreAo){
                    postScoreAoList.add((PostScoreAo) recommendPostScoreAo);
                }
            }

            // 记录推荐过的数据
            List<Long> postIds = postScoreAoList.stream()
                    .map(PostScoreAo::getPostId)
                    .collect(Collectors.toList());
            recommendedRecordsService.recordRecommendedPosts(userId, postIds);
            return postScoreAoList;
        }
        return new ArrayList<>();
    }
}
