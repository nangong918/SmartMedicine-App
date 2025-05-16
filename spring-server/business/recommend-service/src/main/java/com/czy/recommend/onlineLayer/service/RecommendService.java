package com.czy.recommend.onlineLayer.service;

import com.czy.api.domain.ao.feature.FeatureContext;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 15:59
 * RecommendService：传入上下文，userId，postId
 * 上下文
 */
public interface RecommendService {

    /**
     * 获取推荐帖子
     * @param context   上下文
     * @return          推荐帖子id
     */
    List<Long> getRecommendPosts(FeatureContext context);

    /**
     * 最后一步，在线（当前临时上下文）
     * @param context   上下文
     * @return          推荐帖子id
     */
    List<Long> getRecommendPostsByContext(FeatureContext context);

}
