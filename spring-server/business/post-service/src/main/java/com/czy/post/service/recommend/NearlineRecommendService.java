package com.czy.post.service.recommend;

import com.czy.api.domain.ao.recommend.PostScoreAo;
import lombok.NonNull;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/6 14:00
 */
public interface NearlineRecommendService {
    /**
     * 获取近线层的推荐数据
     * @param userId    用户id
     * @param num       推荐数量
     * @return          推荐数据
     */
    List<PostScoreAo> getNearlineRecommend(@NonNull Long userId, int num);
}
