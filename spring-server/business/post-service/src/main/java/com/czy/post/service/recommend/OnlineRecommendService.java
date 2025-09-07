package com.czy.post.service.recommend;

import com.czy.api.domain.ao.recommend.PostScoreAo;
import lombok.NonNull;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/20 16:42
 */
public interface OnlineRecommendService {

    /**
     * 获取在线推荐（返回数量是不确定的，因为是根据当前状态给出的推荐。）
     * @param userId    用户id
     * @return  推荐的帖子
     */
    List<PostScoreAo> getOnlineRecommend(@NonNull Long userId, int num);

}
