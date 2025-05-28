package com.czy.recommend.service.impl;

import com.czy.api.api.offline.OfflineRecommendService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.recommend.RecommendRedisKey;
import com.czy.api.domain.ao.feature.FeatureContext;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.czy.recommend.nearOnlineLayer.service.NearOnlineRecommendService;
import com.czy.recommend.onlineLayer.service.OnlineRecommendService;
import com.czy.recommend.service.RecommendService;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/5/16 17:06
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RecommendServiceImpl implements RecommendService {

    private final RedissonService redissonService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OfflineRecommendService offlineRecommendService;
    private final NearOnlineRecommendService nearOnlineRecommendService;
    private final OnlineRecommendService onlineRecommendService;
    private final Integer TIME_OUT = 3;

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
        // final List
        List<Long> finalRecommendPosts = new ArrayList<>();

        /// 离线层
        // 1. 离线-召回
        List<PostScoreAo> offlineRecommend = offlineRecommendService.getOfflineRecommend(context.getUserId());
        finalRecommendPosts = postScoreAosToIds(offlineRecommend);
        if (finalRecommendPosts.size() >= FeatureConstant.USER_RECOMMEND_GET_NUM){
            return finalRecommendPosts;
        }
        /// 近线层
        /// 在线层
        List<PostScoreAo> nearOnlineAndOnlineRecommend = getRecommends(context);
        finalRecommendPosts.addAll(postScoreAosToIds(nearOnlineAndOnlineRecommend));

        return finalRecommendPosts;
    }

    // 线程池配置（建议作为类成员变量）
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(4);

    // 近线层和在线层同时进行计算，近线层如果超过3秒则继续在线程池计算，但是此函数不等待了，直接返回在线层；
    private List<PostScoreAo> getRecommends(FeatureContext context){
        /// 0. 先尝试从redis获取数据
        if (redissonService.zCount(RecommendRedisKey.NEAR_ONLINE_RESULT_KEY) > 0){
            List<Object> nearlineResults = redissonService.zPopTopNAndRemove(
                    RecommendRedisKey.NEAR_ONLINE_RESULT_KEY
                    , FeatureConstant.USER_RECOMMEND_GET_NUM
            );

            List<PostScoreAo> nearlineScoreAoResults = new ArrayList<>();
            for (Object nearlineResult : nearlineResults) {
                if (nearlineResult instanceof PostScoreAo){
                    nearlineScoreAoResults.add((PostScoreAo) nearlineResult);
                }
            }

            if (nearlineScoreAoResults.size() >= FeatureConstant.USER_RECOMMEND_GET_NUM){
                return nearlineScoreAoResults;
            }
            else {
                List<PostScoreAo> onlineResults = onlineRecommendService.getOnlineRecommend(context);
                return supplementResults(nearlineScoreAoResults, onlineResults, FeatureConstant.USER_RECOMMEND_GET_NUM);
            }
        }

        /// 1. 异步启动在线层和近线层计算
        // 在线层
        CompletableFuture<List<PostScoreAo>> onlineFuture = CompletableFuture.supplyAsync(
                () -> onlineRecommendService.getOnlineRecommend(context),
                asyncExecutor
        );

        // 近线层
        CompletableFuture<List<PostScoreAo>> nearlineFuture = CompletableFuture.supplyAsync(
                () -> nearOnlineRecommendService.getNearOnlineRecommend(context),
                asyncExecutor
        );

        /// 2. 尝试获取近线结果（3秒超时）
        try {
            List<PostScoreAo> nearlineResults = nearlineFuture.get(TIME_OUT, TimeUnit.SECONDS);

            // 3秒内近线完成：
            // 1) 存入Redis
            Map<Object, Double> nearlineMap = nearlineResults.stream()
                    .collect(Collectors.toMap(PostScoreAo::getPostId, PostScoreAo::getScore));
            redissonService.zAddAll(
                    RecommendRedisKey.NEAR_ONLINE_RESULT_KEY,
                    nearlineMap,
                    RecommendRedisKey.NEAR_ONLINE_RESULT_EXPIRE_TIME
            );
            // 2) 从Redis取出并移除前20条
            List<Object> finalResults = redissonService.zPopTopNAndRemove(
                    RecommendRedisKey.NEAR_ONLINE_RESULT_KEY
                    , FeatureConstant.USER_RECOMMEND_GET_NUM
            );

            List<PostScoreAo> nearlineScoreAoResults = new ArrayList<>();
            for (Object nearlineResult : finalResults) {
                if (nearlineResult instanceof PostScoreAo){
                    nearlineScoreAoResults.add((PostScoreAo) nearlineResult);
                }
            }

            // 3) 不足20条时用在线结果补充
            if (nearlineScoreAoResults.size() < FeatureConstant.USER_RECOMMEND_GET_NUM) {
                List<PostScoreAo> onlineResults = onlineFuture.get();
                supplementResults(nearlineScoreAoResults, onlineResults, FeatureConstant.USER_RECOMMEND_GET_NUM);
            }
            return nearlineScoreAoResults;

        }
        catch (TimeoutException e) {
            log.info("近线超时，改为返回在线策略", e);
            try {
                return onlineFuture.get();
            } catch (Exception e1) {
                log.error("在线推荐异常Recommendation error", e1);
                return new ArrayList<>();
            }
        }
        catch (Exception e ) {
            log.error("获取推荐异常", e);
            return new ArrayList<>();
        }
    }

    // 拼接
    private List<PostScoreAo> supplementResults(List<PostScoreAo> finalResults, List<PostScoreAo> onlineResults, int limitNum) {
        for (PostScoreAo onlineResult : onlineResults) {
            if (!finalResults.contains(onlineResult)) {
                finalResults.add(onlineResult);
            }
            if (finalResults.size() >= limitNum) {
                break;
            }
        }
        return finalResults;
    }

    // List<PostScoreAo> -> List<Long>
    private List<Long> postScoreAosToIds(List<PostScoreAo> list) {
        List<Long> postIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)){
            for (PostScoreAo postScoreAo : list) {
                postIds.add(postScoreAo.getPostId());
            }
        }
        return postIds;
    }
}
