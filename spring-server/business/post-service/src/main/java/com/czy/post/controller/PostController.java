package com.czy.post.controller;

import cn.hutool.core.util.IdUtil;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.PostPublishRequest;
import com.czy.api.domain.dto.http.response.PostPublishResponse;
import com.czy.springUtils.service.RedisManagerService;
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
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author 13225
 * @date 2025/4/19 0:26
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@Validated // 启用校验
@RequiredArgsConstructor // 自动注入@Autowired
@RequestMapping(PostConstant.Post_CONTROLLER)
public class PostController {
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final PostConverter postConverter;
    private final RedisManagerService redisManagerService;
    private final RedissonService redissonService;

    // 发布post
    /**
     * 发布post分为2个http，第一个http需要对post的文本信息进行基本的检查
     * 第一步缓存 + 获取雪花id，此处是第一步
     * 此处帖子未发布完成的用户需要上分布式锁，禁止其发布其他帖子。避免造成频繁访问
     * 第二个http请求在oss
     */
    @PostMapping(PostConstant.POST_PUBLISH_FIRST)
    public Mono<BaseResponse<PostPublishResponse>>
    postPublishFirst(@Valid @RequestBody PostPublishRequest request){
        // 1.给用户id上分布式锁
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        if (userDo == null){
            String warningMessage = String.format("用户不存在，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        String userAccount = request.getSenderId();
        // 对userAccount上分布式锁
        // 分布式锁在此上锁，如果出现异常就解锁
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                userAccount,
                PostConstant.POST_PUBLISH_FIRST,
                PostConstant.POST_PUBLISH_KEY_EXPIRE_TIME
        );
        if (!redissonService.tryLock(redissonClusterLock)){
            String warningMessage = String.format("用户正在发布帖子，请稍后再试，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        // 2.缓存到redis
        PostAo postAo = postConverter.requestToAo(request, userDo.getId());
        // 生成雪花id
        long snowflakeId = IdUtil.getSnowflakeNextId();
        // key统一格式：post_publish_key:snowflakeId（注意是snowflakeId不是userAccount或者userName）
        String key = PostConstant.POST_PUBLISH_KEY + snowflakeId;
        redisManagerService.setObjectAsString(postAo, key, PostConstant.POST_PUBLISH_KEY_EXPIRE_TIME);
        PostPublishResponse response = new PostPublishResponse();
        response.setSnowflakeId(snowflakeId);
        return Mono.just(BaseResponse.getResponseEntitySuccess(response));
    }

    // 删除post

    // 修改post

    // 查询post

}
