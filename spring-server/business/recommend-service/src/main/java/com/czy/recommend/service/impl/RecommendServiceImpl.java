package com.czy.recommend.service.impl;

import com.czy.api.api.offline.OfflineRecommendService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.offline.OfflineRedisConstant;
import com.czy.api.constant.recommend.RecommendRedisKey;
import com.czy.api.domain.ao.feature.FeatureContext;
import com.czy.api.domain.ao.recommend.PostScoreAo;
import com.czy.recommend.service.NearOnlineRecommendService;
import com.czy.recommend.service.NearlineRecommendService;
import com.czy.recommend.service.OnlineRecommendService;
import com.czy.recommend.service.RecommendService;
import com.czy.recommend.service.RecommendedRecordsService;
import com.utils.mvc.redisson.RedissonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final NearlineRecommendService nearlineRecommendService;
    private final NearOnlineRecommendService nearOnlineRecommendService;
    private final OnlineRecommendService onlineRecommendService;
    private final RecommendedRecordsService recommendedRecordsService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private final PostSearchService postSearchService;

    // todo 1.完成批量导入 2.完成随机测试接口；用于帮助前端测试数据
    // todo 导图记录各层数据来源, 让推荐系统变得可控，可追踪给
    /**
     * 获取推荐帖子
     * 1. 离线-召回
     * 2. 近线-召回
     * 3. 在线：
     *      3.1 离线-特征
     *      3.2 近线-特征
     *      3.3 在线（当前临时上下文）
     * @param userId   userId
     * @return  推荐帖子
     */
    @Override
    public List<Long> getRecommendPosts(@NonNull Long userId) {
        // final List
        List<Long> finalRecommendPosts = new ArrayList<>();

        /// 离线层  (使用离线数据，不及时响应) todo 离线计算存储到 redis代码检查
        List<PostScoreAo> offlineRecommends = offlineRecommendService.getOfflineRecommend(userId);
        finalRecommendPosts = postScoreAosToIds(offlineRecommends);
        // 检查是否可以推荐
        if (checkAndRecord(userId, finalRecommendPosts)){
            return finalRecommendPosts;
        }

        /// 近线层 （使用在线数据，尽量及时响应） todo 检查是否需要近线计算
        // 计算出差多少个
        int needNum = FeatureConstant.USER_RECOMMEND_GET_NUM - finalRecommendPosts.size();
        List<PostScoreAo> nearlineRecommends = nearlineRecommendService.getNearlineRecommend(userId, needNum);
        finalRecommendPosts.addAll(postScoreAosToIds(nearlineRecommends));
        // 检查是否可以推荐
        if (checkAndRecord(userId, finalRecommendPosts)){
            return finalRecommendPosts;
        }

        /// 在线层 （使用在线数据，保证及时响应） todo 检查在线行为是否存储在redis，在线层需要获取在线数据
        // 计算出差多少个
        int needNum2 = needNum - finalRecommendPosts.size();
        List<PostScoreAo> onlineRecommends = onlineRecommendService.getOnlineRecommend(userId, needNum2);
        finalRecommendPosts.addAll(postScoreAosToIds(onlineRecommends));
        // 检查是否可以推荐
        if (checkAndRecord(userId, finalRecommendPosts)){
            return finalRecommendPosts;
        }

        /// 热门层
        List<Long> heatRecommendIds = getHeatPosts();
        finalRecommendPosts.addAll(heatRecommendIds);
        // 检查是否可以推荐
        if (checkAndRecord(userId, finalRecommendPosts)){
            return finalRecommendPosts;
        }

        /// 随机层
        // user的随机
        // 计算出差多少个
        int needNum3 = needNum2 - finalRecommendPosts.size();
        List<Long> randomPosts = getRandomPosts(userId, needNum3);
        finalRecommendPosts.addAll(randomPosts);
        // 检查是否可以推荐
        if (checkAndRecord(userId, finalRecommendPosts)){
            return finalRecommendPosts;
        }

        // 非user的随机
        int needNum4 = FeatureConstant.USER_RECOMMEND_GET_NUM - finalRecommendPosts.size();
        List<Long> randomPosts2 = getRandomPosts(needNum4);
        finalRecommendPosts.addAll(randomPosts2);

        // 记录推荐过的帖子
        recommendedRecordsService.recordRecommendedPosts(userId, finalRecommendPosts);
        return finalRecommendPosts;
    }

    /**
     * 检查是否可以推荐了
     * @param userId    用户id
     * @param posts     推荐的帖子
     * @return          是否可以推荐
     */
    private boolean checkAndRecord(Long userId, List<Long> posts) {
        // 检查是否满足推荐数量
        if (posts.size() >= FeatureConstant.USER_RECOMMEND_GET_NUM) {
            // 缓存推荐过的帖子
            recommendedRecordsService.recordRecommendedPosts(userId, posts);
            return true;
        }
        return false;
    }

    @Override
    public List<Long> getHeatPosts() {
        // 从redis获取数据
        Map<Object, Double> heatPosts = redissonService.zGetAll(OfflineRedisConstant.OFFLINE_POST_HEAT_KEY);
        if (CollectionUtils.isEmpty(heatPosts)){
            return new ArrayList<>();
        }
        // 将 Map 转换为 List，并按分数从高到低排序
        // 按分数降序排序
        // 提取 Long 类型的帖子 ID
        return heatPosts.entrySet()
                .stream()
                .sorted(Map.Entry.<Object, Double>comparingByValue().reversed()) // 按分数降序排序
                .map(entry -> (Long) entry.getKey()) // 提取 Long 类型的帖子 ID
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getRandomPosts(Long userId, int randomNum) {
        if (randomNum <= 0){
            return new ArrayList<>();
        }

        if (randomNum > FeatureConstant.USER_RECOMMEND_GET_NUM){
            randomNum = FeatureConstant.USER_RECOMMEND_GET_NUM;
        }

        Set<Long> userViewedPostIds = recommendedRecordsService.getUserRecommendedPostRecords(userId);
        return postSearchService.getNotInPostIds(userViewedPostIds, randomNum);
    }

    @Override
    public List<Long> getRandomPosts(int randomNum) {
        return postSearchService.getRandomPosts(randomNum);
    }

    // 线程池配置（建议作为类成员变量）
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(4);

    // 近线层和在线层同时进行计算，近线层如果超过3秒则继续在线程池计算，但是此函数不等待了，直接返回在线层；
    private List<PostScoreAo> getRecommends(FeatureContext context, int needNum){
        /// 0. 先尝试从redis获取数据
        if (redissonService.zCount(RecommendRedisKey.NEAR_ONLINE_RESULT_KEY) > 0){
            List<Object> nearlineResults = redissonService.zPopTopNAndRemove(
                    // 近线层
                    RecommendRedisKey.NEAR_ONLINE_RESULT_KEY,
                    needNum
            );

            List<PostScoreAo> nearlineScoreAoResults = new ArrayList<>();
            for (Object nearlineResult : nearlineResults) {
                if (nearlineResult instanceof PostScoreAo){
                    nearlineScoreAoResults.add((PostScoreAo) nearlineResult);
                }
            }

            // 检查是否满足拿到了制定条数
            if (nearlineScoreAoResults.size() >= needNum){
                return nearlineScoreAoResults;
            }
            else {
                // 计算还差多少个
                int needNum2 = needNum - nearlineScoreAoResults.size();
                List<PostScoreAo> onlineResults = onlineRecommendService.getOnlineRecommend(context.getUserId(), needNum2);
                return supplementResults(nearlineScoreAoResults, onlineResults, needNum2);
            }
        }

        /// 1. 异步启动在线层和近线层计算
        // 在线层
        CompletableFuture<List<PostScoreAo>> onlineFuture = CompletableFuture.supplyAsync(
                () -> onlineRecommendService.getOnlineRecommend(context.getUserId(), 1),
                asyncExecutor
        );

        // 近线层
        CompletableFuture<List<PostScoreAo>> nearlineFuture = CompletableFuture.supplyAsync(
                () -> nearOnlineRecommendService.getNearOnlineRecommend(context),
                asyncExecutor
        );

        /// 2. 尝试获取近线结果（3秒超时）
        try {
            Integer TIME_OUT = 3;
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
