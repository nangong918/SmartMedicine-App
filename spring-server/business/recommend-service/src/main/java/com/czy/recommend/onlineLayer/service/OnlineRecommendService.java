package com.czy.recommend.onlineLayer.service;

import com.czy.api.domain.ao.feature.FeatureContext;
import com.czy.api.domain.ao.recommend.PostScoreAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/20 16:42
 */
public interface OnlineRecommendService {

    List<PostScoreAo> getOnlineRecommend(FeatureContext context);

}
