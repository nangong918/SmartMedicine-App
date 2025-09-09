package com.czy.post.controller;

import com.czy.api.api.post.PostNerService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.api.user.user.UserService;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.ao.post.CommentAo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.GetCommentRequest;
import com.czy.api.domain.dto.http.request.GetPostPreviewListRequest;
import com.czy.api.domain.dto.http.request.GetSinglePostRequest;
import com.czy.api.domain.dto.http.request.PostPublishRequest;
import com.czy.api.domain.dto.http.request.PostUpdateRequest;
import com.czy.api.domain.dto.http.response.GetPostCommentsResponse;
import com.czy.api.domain.dto.http.response.GetPostPreviewListResponse;
import com.czy.api.domain.dto.http.response.PostPublishResponse;
import com.czy.api.domain.dto.http.response.SinglePostResponse;
import com.czy.api.domain.vo.post.PostPreviewVo;
import com.czy.api.domain.vo.post.PostVo;
import com.czy.api.domain.vo.post.toFront.PostFVo;
import com.czy.api.domain.vo.post.toFront.PostPreviewFVo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.PostExceptions;
import com.czy.api.exception.UserExceptions;
import com.czy.post.front.PostFrontService;
import com.czy.post.service.PostCommentService;
import com.czy.post.service.PostService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    private final PostCommentService postCommentService;
    private final PostNerService postNerService;
    private final PostSearchService postSearchService;
    private final PostFrontService postFrontService;

    // 发布post todo 整合为一个service，不在controller做复杂逻辑
    /**
     * 发布post分为2个http，第一个http需要对post的文本信息进行基本的检查
     * 第一步缓存 + 获取雪花id，此处是第一步
     * 此处帖子未发布完成的用户需要上分布式锁，禁止其发布其他帖子。避免造成频繁访问
     * 第二个http请求在oss
     * <p>
     * 额外补充1：埋点事件
     * <p>
     * 额外补充2：特征提取 + 存储：
     * post用acTree自动机对比词典，查询是否存在关键词
     * 将关键词结果存入对应的neo4j
     * 待完成发布的时候将帖子的内容转为特征值，并将特征值存入Neo4j
     * AcTree的速度很快，可以对全文进行实体检测大概是10ms
     */
    @PostMapping(PostConstant.POST_PUBLISH_FIRST)
    public BaseResponse<PostPublishResponse>
    postPublishFirst(@Valid @RequestBody PostPublishRequest request){
        long snowflakeId;
        PostAo postAo = postConverter.requestToAo(request, request.getSenderId());
        // 审核 目前只有防止刷帖；没有自然语言审核
        if (!postService.isLegalPost(postAo)) {
            return BaseResponse.LogBackError(PostExceptions.POST_CONTENT_REJECTED);
        }
        // 自然语言标签分析 + 标签存储 todo 4大基本分区
        // 不需要上传文件的情况
        if (!request.getIsHaveFiles()){
            snowflakeId = postService.releasePostWithoutFile(postAo);
            log.info("[post不包含文件发布][帖子id为：{}]", snowflakeId);
        }
        else {
            // 1.给用户id上分布式锁
            // 对userId上分布式锁
            // 选择userId是因为oss那边只知道userId，对userAccount无感知
            // 分布式锁在此上锁，如果出现异常就解锁
            // 解锁在整个流程任何地方出现异常以及结束
            RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                    String.valueOf(request.getSenderId()),
                    PostConstant.Post_CONTROLLER + PostConstant.POST_PUBLISH_FIRST,
                    PostConstant.POST_CHANGE_KEY_EXPIRE_TIME
            );
            if (!redissonService.tryLock(redissonClusterLock)){
                String warningMessage = String.format("用户正在发布帖子，请稍后再试，account: %s", request.getSenderId());
                return BaseResponse.LogBackError(warningMessage);
            }
            try {
                // 2.缓存到redis
                // 2.1特征提取
                // 使用知识图谱实体 + AcTree进行知识图谱特征提取
                List<PostNerResult> resultList = postNerService.getPostNerResults(postAo.getTitle());

                log.info("[post包含文件发布][userId: {}][发布post title: {}]\n[nerSize: {}][nerResults: {}]",
                        request.getSenderId(), request.getTitle(), resultList.size(), resultList);

                // acTree 进行Topic特征提取 todo
                postAo.setNerResults(resultList);
                // 特征存储在mongodb；mysql不适合存储非结构化数据
                // redis + 生成雪花id
                // 2.2存储到redis并生成雪花id返回
                snowflakeId = postService.releasePostFirst(postAo);
            } catch (Exception e) {
                // 任何异常都直接解除分布式锁
                redissonService.unlock(redissonClusterLock);
                if (e instanceof AppException){
                    // 交给全局或异常处理
                    throw new AppException(e.getMessage());
                }
                log.error("发布文章异常", e);
                return BaseResponse.LogBackError(e.getMessage());
            }
            // key统一格式：post_publish_key:snowflakeId（注意是snowflakeId不是userAccount或者userName）
        }
        PostPublishResponse response = new PostPublishResponse();
        response.setSnowflakeId(snowflakeId);
        return BaseResponse.getResponseEntitySuccess(response);
    }

    // 删除post todo 删除数据需要把评论数据一并删除
    @DeleteMapping("/postDelete")
    public BaseResponse<String> deletePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        if (postId == null || userId == null){
            return BaseResponse.LogBackError(CommonExceptions.PARAM_ERROR);
        }
        postService.deletePost(postId, userId);
        return BaseResponse.getResponseEntitySuccess("删除成功");
    }

    // 修改post [后续开发]
    // 只修改内容
    @PostMapping("/postUpdate")
    public BaseResponse<String>
    updatePost(@Valid @RequestBody PostUpdateRequest request){
        PostAo postAo = postConverter.updateRequestToAo(request, request.getSenderId());
        postAo.setId(request.getPostId());
        postService.updatePostInfoAndContent(postAo);
        return BaseResponse.getResponseEntitySuccess("修改成功");
    }

    // 修改了全部 [后续开发]
    @PostMapping(PostConstant.POST_UPDATE_ALL)
    public BaseResponse<String>
    updatePostAll(@Valid @RequestBody PostUpdateRequest request){
        // 1.给用户id上分布式锁

        // 获取分布式锁
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                String.valueOf(request.getSenderId()),
                PostConstant.Post_CONTROLLER + PostConstant.POST_UPDATE_ALL,
                PostConstant.POST_CHANGE_KEY_EXPIRE_TIME
        );
        if (!redissonService.tryLock(redissonClusterLock)){
            String warningMessage = String.format("用户正在修改帖子，请稍后再试，account: %s", request.getSenderId());
            return BaseResponse.LogBackError(warningMessage);
        }
        // try-catch优先级高于全局异常
        try {
            PostAo postAo = postConverter.updateRequestToAo(request, request.getSenderId());
            postService.updatePostFirst(postAo, request.getPostId());
        } catch (Exception e){
            // 出现任何异常都直接解除分布式锁
            redissonService.unlock(redissonClusterLock);
            // 如果是App异常，就抛出交给全局异常处理器，然后交给前端
            if (e instanceof AppException){
                throw new AppException(e.getMessage());
            }
        }

        return BaseResponse.getResponseEntitySuccess("修改申请已提交，请等待");
    }

    @PostMapping("/getPostInfoList")
    public BaseResponse<GetPostPreviewListResponse>
    getPostsNew(@Valid @RequestBody GetPostPreviewListRequest request){
        Long userId = request.getUserId();
        if (!userService.checkUserExist(userId)){
            return BaseResponse.LogBackError(UserExceptions.USER_NOT_EXIST);
        }

        List<Long> postIds = request.getPostIds();
        if (CollectionUtils.isEmpty(postIds)){
            return BaseResponse.LogBackError(CommonExceptions.PARAM_ERROR);
        }

        List<PostPreviewVo> postPreviewVos = postFrontService.getPostPreviewVoListByIds(
                postIds,
                userId
        );
        List<PostPreviewFVo> postPreviewFVos = postFrontService.getPostPreviewFVos(postPreviewVos);

        GetPostPreviewListResponse response = new GetPostPreviewListResponse();
        response.setPostPreviewVos(postPreviewFVos);

        return BaseResponse.getResponseEntitySuccess(response);
    }


    @PostMapping("/getPost")
    public BaseResponse<SinglePostResponse>
    getPostNew(@Valid @RequestBody GetSinglePostRequest request){
        if (ObjectUtils.isEmpty(request.getPostId())){
            return BaseResponse.LogBackError(CommonExceptions.PARAM_ERROR);
        }

        if (ObjectUtils.isEmpty(request.getPageNum()) || request.getPageNum() < 1){
            request.setPageNum(1);
        }
        List<CommentAo> commentAos = postCommentService.getLevel1PostCommentAos(
                request.getPostId(), PostConstant.COMMENT_PAGE_SIZE, request.getPageNum()
        );

        SinglePostResponse singlePostResponse = new SinglePostResponse();
        PostVo postVo = postFrontService.getPostVo(request.getPostId(), request.getUserId());
        singlePostResponse.postVo = Optional.ofNullable(postVo).map(PostFVo::new).orElse(null);
        singlePostResponse.commentAos = commentAos;
        return BaseResponse.getResponseEntitySuccess(singlePostResponse);
    }

    // 获取下拉一级评论（pageNum）
    @PostMapping("/getPostComments")
    public BaseResponse<GetPostCommentsResponse>
    getPostLevel1Comments(@Validated @RequestBody GetCommentRequest request){
        return getPostComments(request.getPostId(), request.getLevel1commentId(),
                request.getPageSize(), request.getPageNum());
    }

    private BaseResponse<GetPostCommentsResponse> getPostComments(
            Long postId,
            @Nullable Long replyCommentId,
            Integer pageSize,
            Integer pageNum) {
        if (postId == null) {
            return BaseResponse.LogBackError(CommonExceptions.PARAM_ERROR);
        }

        List<CommentAo> commentAos;
        // 没有level1Id默认为他就是level1评论
        if (replyCommentId == null) {
            commentAos = postCommentService.getLevel1PostCommentAos(postId, pageSize, pageNum);
        }
        else {
            commentAos = postCommentService.getLevel2PostCommentAos(postId, replyCommentId, pageSize, pageNum);
        }

        GetPostCommentsResponse getPostCommentsResponse = new GetPostCommentsResponse();
        getPostCommentsResponse.commentAos = commentAos;

        return BaseResponse.getResponseEntitySuccess(getPostCommentsResponse);
    }

    // 发表评论 逻辑在postHandler实现
/*    @Deprecated
    @PostMapping("/comment")
    public BaseResponse<BaseHttpResponse> comment(@Validated @RequestBody CommentRequest request) {

    }*/
}
