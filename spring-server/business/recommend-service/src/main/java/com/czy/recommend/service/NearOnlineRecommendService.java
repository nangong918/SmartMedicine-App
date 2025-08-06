package com.czy.recommend.service;

import com.czy.api.domain.ao.feature.FeatureContext;
import com.czy.api.domain.ao.recommend.PostScoreAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/20 15:36
 */
public interface NearOnlineRecommendService {

    List<PostScoreAo> getNearOnlineRecommend(FeatureContext context);

}
