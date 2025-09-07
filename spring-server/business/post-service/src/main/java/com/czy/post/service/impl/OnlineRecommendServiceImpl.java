package com.czy.post.service.impl;

import com.czy.api.api.feature.PostFeatureService;
import com.czy.api.api.feature.UserFeatureService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.domain.ao.UserOnlineFeatureAo;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.czy.post.service.recommend.OnlineRecommendService;
import com.czy.post.service.recommend.RecommendedRecordsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/5/20 16:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OnlineRecommendServiceImpl implements OnlineRecommendService {

    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserFeatureService userFeatureService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostFeatureService postFeatureService;
    private final PostSearchService postSearchService;
    private final RecommendedRecordsService recommendedRecordsService;

    @Override
    public List<PostScoreAo> getOnlineRecommend(@NonNull Long userId, int num) {
        // user特征
        UserOnlineFeatureAo userTempFeatureAo = userFeatureService.getCacheUserOnlineFeature(userId);
        Map<String, Double> entityScoreMap = Optional.ofNullable(userTempFeatureAo)
                .map(UserOnlineFeatureAo::getEntityScoreMap)
                .orElse(new HashMap<>());

        // 特征词找到的post及其分数map
        Map<Long, PostScoreAo> postScoreAoMap = postSearchService.searchPostIdsByLikeTitle(entityScoreMap);

        // 获取已经推荐的帖子
        Set<Long> recommendedPostIds = recommendedRecordsService.getUserRecommendedPostRecords(userId);

        // 最终推荐帖子ID列表
        List<Long> recommendPostIdsList = new ArrayList<>(postScoreAoMap.keySet());

        // 过滤已经推荐的帖子
        recommendPostIdsList.removeAll(recommendedPostIds);

        // 去重并累加分数
        Map<Long, PostScoreAo> finalPostScoreAoMap = new HashMap<>();
        for (Long postId : recommendPostIdsList) {
            PostScoreAo postScoreAo = postScoreAoMap.get(postId);
            if (postScoreAo != null) {
                finalPostScoreAoMap.merge(postId, postScoreAo, (existing, newPost) -> {
                    existing.setScore(existing.getScore() + 1.0);
                    return existing;
                });
            }
        }

        // 将结果转换为列表
        List<PostScoreAo> finalPostScoreAos = new ArrayList<>(finalPostScoreAoMap.values());

        // 排序
        finalPostScoreAos.sort((o1, o2) -> o2.getScore().compareTo(o1.getScore()));

        // 限制返回数量
        return finalPostScoreAos.stream().limit(num).collect(Collectors.toList());
    }
}
