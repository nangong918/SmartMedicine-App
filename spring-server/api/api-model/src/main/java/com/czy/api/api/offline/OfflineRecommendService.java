package com.czy.api.api.offline;

import com.czy.api.domain.ao.recommend.PostScoreAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/20 14:08
 */

public interface OfflineRecommendService {

    List<PostScoreAo> getOfflineRecommend(Long userId);

}
