package com.czy.recommend.onlineLayer.service.impl;

import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.domain.ao.auth.UserTempFeatureAo;
import com.czy.api.domain.ao.feature.FeatureContext;
import com.czy.recommend.onlineLayer.service.RecommendService;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 17:06
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendServiceImpl implements RecommendService {

    private final RedissonService redissonService;

    /**
     * 获取推荐帖子
     * 1. 离线-召回
     * 2. 近线-召回
     * 3. 在线：
     *      3.1 离线-特征
     *      3.2 近线-特征
     *      3.3 在线（当前临时上下文）
     * @param context   上下文
     * @return
     */
    @Override
    public List<Long> getRecommendPosts(FeatureContext context) {
        /// 离线层
        // 1. 离线-召回
        /// 近线层
        // 2. 近线-召回
        /// 在线层
        // 3. 离线-特征
        UserTempFeatureAo userTempFeatureAo = new UserTempFeatureAo();
        Object userHistoryFeature = redissonService.getObjectFromHashMap(
                OfflineRedisConstant.USER_HISTORY_FEATURE_KEY,
                String.valueOf(context.getUserId())
        );
        if (userHistoryFeature instanceof UserTempFeatureAo){
            if (!((UserTempFeatureAo) userHistoryFeature).isEmpty()){

            }
        }
        // 4. 近线-特征
        // 5. 在线（当前临时上下文）
        List<Long> postIds = getRecommendPostsByContext(context);
        return null;
    }

    @Override
    public List<Long> getRecommendPostsByContext(FeatureContext context) {
        Long userId = context.getUserId();
        return null;
    }
}
