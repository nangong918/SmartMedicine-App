package com.czy.post.controller;

import com.czy.api.api.post.PostSearchService;
import com.czy.api.api.user_relationship.user.UserService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.recommend.RecommendConstant;
import com.czy.api.constant.recommend.RecommendRedisKey;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.RecommendPostRequest;
import com.czy.api.domain.dto.http.response.RecommendPostResponse;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.UserExceptions;
import com.czy.post.service.recommend.RecommendService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/24 11:28
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(RecommendConstant.POST_RECOMMEND_CONTROLLER)
public class RecommendController {

    private final RecommendService recommendService;
    private final PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final RedissonService redissonService;

    // 推荐帖子
    @PostMapping(RecommendConstant.RECOMMEND_POSTS)
    public BaseResponse<RecommendPostResponse>
    recommendPosts(@Validated @RequestBody RecommendPostRequest request) {
        Long userId = request.getUserId();

        UserDo userDo = userService.getUserById(userId);
        if (userDo == null || userDo.getId() == null){
            return BaseResponse.LogBackError(UserExceptions.USER_NOT_EXIST);
        }

        // 1.用于检查单次推荐的分布式锁
        RedissonClusterLock singleRecommendLock = new RedissonClusterLock(
                String.valueOf(userId),
                RecommendConstant.POST_RECOMMEND_CONTROLLER + RecommendConstant.RECOMMEND_POSTS,
                RecommendRedisKey.clickRecommendLockTimeout
        );

        if (!redissonService.tryLock(singleRecommendLock)){
            return BaseResponse.LogBackError(CommonExceptions.FREQUENTLY_CLICK, "用户正在推荐帖子，请稍后再试");
        }

        // 2.检查是否频繁点击推荐
        String clickRecommendTimesKey = RecommendRedisKey.clickRecommendTimesKey + userId;
        Integer clickRecommendTimes = redissonService.incrementInteger(
                clickRecommendTimesKey,
                1,
                RecommendRedisKey.clickRecommendTimesSaveTimeout
                );
        if (clickRecommendTimes > RecommendRedisKey.clickRecommendTimesMax){
            // 3.获取冷静锁
            RedissonClusterLock clickRecommendLock = new RedissonClusterLock(
                    clickRecommendTimesKey,
                    RecommendRedisKey.clickRecommendSleepTimeout
            );
            // 此分布式锁只等其自动消失，不解锁
            if (!redissonService.tryLock(clickRecommendLock)){
                return BaseResponse.LogBackError(CommonExceptions.FREQUENTLY_CLICK,"请耐心等待，请稍后再试");
            }
            return BaseResponse.LogBackError(CommonExceptions.FREQUENTLY_CLICK,"用户点击推荐次数过多，请稍后再试");
        }

        try {
            long startTime = System.currentTimeMillis();
            List<Long> recommendPostIdList = recommendService.getRecommendPosts(request.getUserId());
            List<PostInfoUrlAo> postInfoUrlAos = postSearchService.getPostInfoUrlAos(recommendPostIdList);
            RecommendPostResponse response = new RecommendPostResponse();
            response.setPostInfoUrlAos(postInfoUrlAos);
            long endTime = System.currentTimeMillis();
            log.info("用户{}推荐帖子耗时{}ms", userDo.getAccount(), endTime - startTime);
            return BaseResponse.getResponseEntitySuccess(response);
        } finally {
            // 解除单次推荐的分布式锁
            redissonService.unlock(singleRecommendLock);
        }
    }

    // 测试用的随机post
    @PostMapping("/test")
    public BaseResponse<RecommendPostResponse>
    testRecommendRandomPosts(@Validated @RequestBody RecommendPostRequest request){
        Long userId = request.getUserId();

        UserDo userDo = userService.getUserById(userId);
        if (userDo == null || userDo.getId() == null){
            return BaseResponse.LogBackError(UserExceptions.USER_NOT_EXIST);
        }

        // 过滤用户看过的内容的random帖子
        List<Long> filterUserViewedPostIds = recommendService.getRandomPosts(userId, FeatureConstant.USER_RECOMMEND_GET_NUM);
        log.info("过滤用户看过的内容的random帖子：{}", filterUserViewedPostIds);
        if (CollectionUtils.isEmpty(filterUserViewedPostIds) || filterUserViewedPostIds.size() < FeatureConstant.USER_RECOMMEND_GET_NUM){
            filterUserViewedPostIds.addAll(
                    recommendService.getRandomPosts(
                            FeatureConstant.USER_RECOMMEND_GET_NUM - filterUserViewedPostIds.size()
                    )
            );
        }

        long startTime = System.currentTimeMillis();
        List<PostInfoUrlAo> postInfoUrlAos = postSearchService.getPostInfoUrlAos(filterUserViewedPostIds);
        log.info("testRecommendRandomPosts::getPostInfoUrlAos time: {}", System.currentTimeMillis() - startTime);
        RecommendPostResponse response = new RecommendPostResponse();
        response.setPostInfoUrlAos(postInfoUrlAos);
        return BaseResponse.getResponseEntitySuccess(response);
    }
}
