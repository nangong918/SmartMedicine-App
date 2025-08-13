package com.czy.post.service.impl;

import com.czy.api.constant.recommend.RecommendRedisKey;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.czy.post.service.recommend.NearlineRecommendService;
import com.utils.redisson.service.RedissonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/6 14:02
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NearlineRecommendServiceImpl implements NearlineRecommendService {

    private final RedissonService redissonService;


    @Override
    public List<PostScoreAo> getNearlineRecommend(@NonNull Long userId, int num) {
        // 获取缓存在redis的推荐帖子id
        String nearlineRecommendKey = RecommendRedisKey.NEARLINE_RECOMMEND_KEY + userId;

        // 获取近线层计算出来的帖子
        if (redissonService.zCount(nearlineRecommendKey) > 0){
            // redis中取出指定数量的post, 并且从未推荐的post记录中删除
            List<Object> recommendPostScoreAos = redissonService.zPopTopNAndRemove(
                    nearlineRecommendKey, num
            );

            List<PostScoreAo> postScoreAoList = new ArrayList<>();
            if (CollectionUtils.isEmpty(recommendPostScoreAos)){
                return new ArrayList<>();
            }

            // 填充评分postAo
            for (Object recommendPostScoreAo : recommendPostScoreAos){
                if (recommendPostScoreAo instanceof PostScoreAo){
                    postScoreAoList.add((PostScoreAo) recommendPostScoreAo);
                }
            }

            // 检查redis库存是否需要执行近线计算 (任务计算任务通过消息队列交给离线层，让离线层去立即执行近线层的任务，而不是像离线数据一样晚上再跑)
            if (postScoreAoList.size() < num || redissonService.zCount(nearlineRecommendKey) < 0){
                // 此处是异步调用，而不是同步调用。所以应该使用mq而不是dubbo；
                // 由于用户每次点击之后处理的实例可能都不是同一实例，所以需要使用mq而不是jvm的线程池
                // todo
            }

            return postScoreAoList;
        }
        return new ArrayList<>();
    }

}
