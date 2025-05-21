package com.czy.recommend.onlineLayer.service.impl;

import com.czy.api.api.feature.PostFeatureService;
import com.czy.api.api.feature.UserFeatureService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.domain.ao.feature.FeatureContext;
import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.czy.recommend.onlineLayer.service.OnlineRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;

    @Override
    public List<PostScoreAo> getOnlineRecommend(FeatureContext context) {
//        Long userId = context.getUserId();
//        List<Map<String, Double>> userOnlineFeature = userFeatureService.getUserOnlineFeature(userId);
        List<Long> postIds = context.getPostIds();
        Map<Long, PostFeatureAo> postFeatures = postFeatureService.getPostFeatures(postIds);
        Set<String> entitySet = postFeatureAosToMap(postFeatures);
        List<PostScoreAo> postScoreAos = new ArrayList<>();
        List<Long> recommendPostIdsList = new ArrayList<>();
        for (String entity : entitySet){
            List<Long> recommendPostIds = postSearchService.searchPostIdsByLikeTitle(entity);
            recommendPostIdsList.addAll(recommendPostIds);
        }

        // Long -> PostScoreAo (重复的 score += 1.0)
        Map<Long, PostScoreAo> postScoreAoMap = recommendPostIdsList.stream()
                .map(postId -> new PostScoreAo(postId, 1.0))
                .collect(Collectors.toMap(PostScoreAo::getPostId, postScoreAo -> postScoreAo));

        for (Map.Entry<Long, PostScoreAo> entry : postScoreAoMap.entrySet()) {
            Long postId = entry.getKey();
            PostScoreAo postScoreAo = entry.getValue();
            if (postFeatures.containsKey(postId)){
                PostFeatureAo postFeatureAo = postFeatures.get(postId);
                for (PostNerResult nerResult : postFeatureAo.getPostNerResultList()){
                    if (entitySet.contains(nerResult.getKeyWord())){
                        postScoreAo.setScore(postScoreAo.getScore() + 1.0);
                    }
                }
            }
            postScoreAos.add(postScoreAo);
        }

        // 排序
        postScoreAos.sort((o1, o2) -> o2.getScore().compareTo(o1.getScore()));

        // TODO 去重

        return postScoreAos;
    }

    private Set<String> postFeatureAosToMap(Map<Long, PostFeatureAo> postFeatureAos){
        Set<String> entitySet = new HashSet<>();
        for (Map.Entry<Long, PostFeatureAo> entry : postFeatureAos.entrySet()) {
            PostFeatureAo postFeatureAo = entry.getValue();
            for (PostNerResult nerResult : postFeatureAo.getPostNerResultList()){
                entitySet.add(nerResult.getKeyWord());
            }
        }
        return entitySet;
    }
}
