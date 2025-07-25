package com.czy.recommend.controller;

import com.czy.api.api.post.PostSearchService;
import com.czy.api.api.user_relationship.UserService;
import com.czy.api.constant.recommend.RecommendConstant;
import com.czy.api.constant.recommend.RecommendRedisKey;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostInfoUrlAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.RecommendPostRequest;
import com.czy.api.domain.dto.http.response.RecommendPostResponse;
import com.czy.api.exception.UserExceptions;
import com.czy.recommend.service.RecommendService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
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
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(RecommendConstant.POST_RECOMMEND_CONTROLLER)
public class RecommendController {

    private final RecommendService recommendService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final RedissonService redissonService;

    // 推荐帖子
    @PostMapping(RecommendConstant.RECOMMEND_POSTS)
    public BaseResponse<RecommendPostResponse>
    recommendPosts(@Validated @RequestBody RecommendPostRequest request) {
        Long userId = request.getFeatureContext().getUserId();

        UserDo userDo = userService.getUserById(userId);
        if (userDo == null || userDo.getId() == null){
            return BaseResponse.LogBackError(UserExceptions.USER_NOT_EXIST);
        }

        // 1.用于检查单次推荐的分布式锁
        RedissonClusterLock singleRecommendLock = new RedissonClusterLock(
                String.valueOf(userId),
                RecommendConstant.serviceRoute + RecommendConstant.RECOMMEND_POSTS,
                RecommendRedisKey.clickRecommendLockTimeout
        );

        if (!redissonService.tryLock(singleRecommendLock)){
            return BaseResponse.LogBackError("用户正在推荐帖子，请稍后再试");
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
                return BaseResponse.LogBackError("请耐心等待，请稍后再试");
            }
            return BaseResponse.LogBackError("用户点击推荐次数过多，请稍后再试");
        }

        try {
            long startTime = System.currentTimeMillis();
            List<Long> recommendPostIdList = recommendService.getRecommendPosts(request.getFeatureContext());
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

}
