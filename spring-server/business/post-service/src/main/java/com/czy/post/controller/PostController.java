package com.czy.post.controller;

import com.czy.api.api.user.UserService;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.GetPostRequest;
import com.czy.api.domain.dto.http.request.PostPublishRequest;
import com.czy.api.domain.dto.http.request.PostUpdateRequest;
import com.czy.api.domain.dto.http.response.GetPostResponse;
import com.czy.api.domain.dto.http.response.PostPublishResponse;
import com.czy.post.service.PostService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

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
    private final RedissonService redissonService;
    private final PostService postService;

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
        // 对userId上分布式锁
        // 选择userId是因为oss那边只知道userId，对userAccount无感知
        // 分布式锁在此上锁，如果出现异常就解锁
        // 解锁在整个流程任何地方出现异常以及结束
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                String.valueOf(userDo.getId()),
                PostConstant.Post_CONTROLLER + PostConstant.POST_PUBLISH_FIRST,
                PostConstant.POST_CHANGE_KEY_EXPIRE_TIME
        );
        if (!redissonService.tryLock(redissonClusterLock)){
            String warningMessage = String.format("用户正在发布帖子，请稍后再试，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        // 2.缓存到redis
        PostAo postAo = postConverter.requestToAo(request, userDo.getId());
        // redis + 生成雪花id
        long snowflakeId = postService.releasePostFirst(postAo);
        // key统一格式：post_publish_key:snowflakeId（注意是snowflakeId不是userAccount或者userName）
        PostPublishResponse response = new PostPublishResponse();
        response.setSnowflakeId(snowflakeId);
        return Mono.just(BaseResponse.getResponseEntitySuccess(response));
    }

    // 删除post
    @DeleteMapping("/postDelete")
    public Mono<BaseResponse<String>> deletePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        if (postId == null || userId == null){
            return Mono.just(BaseResponse.LogBackError("参数错误", log));
        }
        postService.deletePost(postId, userId);
        return Mono.just(BaseResponse.getResponseEntitySuccess("删除申请已提交，请等待"));
    }

    // 修改post
    // 只修改内容
    @PostMapping("/postUpdate")
    public Mono<BaseResponse<String>>
    updatePost(@Valid @RequestBody PostUpdateRequest request){
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        if (userDo == null){
            String warningMessage = String.format("用户不存在，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        PostAo postAo = postConverter.updateRequestToAo(request, userDo.getId());
        postAo.setId(request.getPostId());
        postService.updatePostInfoAndContent(postAo);
        return Mono.just(BaseResponse.getResponseEntitySuccess("修改申请已提交，请等待"));
    }

    // 修改了全部
    @PostMapping(PostConstant.POST_UPDATE_ALL)
    public Mono<BaseResponse<String>>
    updatePostAll(@Valid @RequestBody PostUpdateRequest request){
        // 1.给用户id上分布式锁
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        if (userDo == null){
            String warningMessage = String.format("用户不存在，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                String.valueOf(userDo.getId()),
                PostConstant.Post_CONTROLLER + PostConstant.POST_UPDATE_ALL,
                PostConstant.POST_CHANGE_KEY_EXPIRE_TIME
        );
        if (!redissonService.tryLock(redissonClusterLock)){
            String warningMessage = String.format("用户正在修改帖子，请稍后再试，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        PostAo postAo = postConverter.updateRequestToAo(request, userDo.getId());
        postService.updatePostFirst(postAo, request.getPostId());
        return Mono.just(BaseResponse.getResponseEntitySuccess("修改申请已提交，请等待"));
    }

    // 查询post
    // 响应体应该包含：postInfo，post-fileIds，postDetails
    // 通过list<postId>查询post消息;
    @PostMapping("/getPosts")
    public Mono<BaseResponse<GetPostResponse>>
    getPosts(@Valid @RequestBody GetPostRequest request){
        List<Long> postIds = request.getPostIds();
        if (CollectionUtils.isEmpty(postIds)){
            return Mono.just(BaseResponse.LogBackError("参数错误", log));
        }
        List<PostAo> postAoList = postService.findPostsByIdList(postIds);
        GetPostResponse getPostResponse = new GetPostResponse();
        getPostResponse.setPostAos(postAoList);
        return Mono.just(BaseResponse.getResponseEntitySuccess(getPostResponse));
    }

    // 如何从各种数据查询List<postId>的逻辑在postSearchService中
}
