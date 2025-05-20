package com.offline.recommend.service.impl;


import com.czy.api.api.offline.OfflineRecommendService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<PostScoreAo> getOfflineRecommend(Long userId) {
        String userRecommendKey = OfflineRedisConstant.USER_RECOMMEND_KEY + ":" + userId;
        if (redissonService.zCount(userRecommendKey) > 0){
            List<Object> recommendPostScoreAos = redissonService.zPopTopNAndRemove(userRecommendKey, FeatureConstant.USER_RECOMMEND_GET_NUM);
            List<PostScoreAo> postScoreAoList = new ArrayList<>();
            if (CollectionUtils.isEmpty(recommendPostScoreAos)){
                return new ArrayList<>();
            }
            for (Object recommendPostScoreAo : recommendPostScoreAos){
                if (recommendPostScoreAo instanceof PostScoreAo){
                    postScoreAoList.add((PostScoreAo) recommendPostScoreAo);
                }
            }
            return postScoreAoList;
        }
        return new ArrayList<>();
    }
}
