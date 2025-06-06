package com.czy.post.controller;

import com.czy.api.api.oss.OssService;
import com.czy.api.api.post.PostNerService;
import com.czy.api.api.post.PostSearchService;
import com.czy.api.api.user.UserService;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.PostCommentConverter;
import com.czy.api.converter.domain.post.PostConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.ao.post.PostCommentAo;
import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.PostCommentDto;
import com.czy.api.domain.dto.http.request.GetPostInfoListRequest;
import com.czy.api.domain.dto.http.request.GetPostPreviewListRequest;
import com.czy.api.domain.dto.http.request.PostPublishRequest;
import com.czy.api.domain.dto.http.request.PostUpdateRequest;
import com.czy.api.domain.dto.http.response.GetPostCommentsResponse;
import com.czy.api.domain.dto.http.response.GetPostInfoListResponse;
import com.czy.api.domain.dto.http.response.GetPostPreviewListResponse;
import com.czy.api.domain.dto.http.response.GetPostResponse;
import com.czy.api.domain.dto.http.response.PostPublishResponse;
import com.czy.api.domain.dto.http.response.SinglePostResponse;
import com.czy.api.domain.vo.CommentVo;
import com.czy.api.domain.vo.PostPreviewVo;
import com.czy.api.domain.vo.PostVo;
import com.czy.post.front.PostFrontService;
import com.czy.post.service.PostCommentService;
import com.czy.post.service.PostService;
import com.utils.mvc.redisson.RedissonClusterLock;
import com.utils.mvc.redisson.RedissonService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final PostCommentConverter postCommentConverter;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private OssService ossService;
    private final PostNerService postNerService;
    private final PostSearchService postSearchService;
    private final PostFrontService postFrontService;

    // 发布post
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
    public Mono<BaseResponse<PostPublishResponse>>
    postPublishFirst(@Valid @RequestBody PostPublishRequest request){
        long snowflakeId;
        // 查询用户是否存在
        UserDo userDo = userService.getUserByAccount(request.getSenderId());
        if (userDo == null){
            String warningMessage = String.format("用户不存在，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        PostAo postAo = postConverter.requestToAo(request, userDo.getId());
        // 审核
        if (!postService.isLegalPost(postAo)) {
            return Mono.just(BaseResponse.LogBackError("帖子内容不合规，请修改"));
        }
        // 不需要上传文件的情况
        if (!request.getIsHaveFiles()){
            snowflakeId = postService.releasePostWithoutFile(postAo);
        }
        else {
            // 1.给用户id上分布式锁
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
            // 2.1特征提取
            // 使用知识图谱实体 + AcTree进行特征提取
            List<PostNerResult> resultList = postNerService.getPostNerResults(postAo.getTitle());
            postAo.setNerResults(resultList);
            // 特征存储在mongodb；mysql不适合存储非结构化数据
            // redis + 生成雪花id
            try {
                // 2.2存储到redis并生成雪花id返回
                snowflakeId = postService.releasePostFirst(postAo);
            } catch (Exception e) {
                // 任何异常都直接解除分布式锁
                redissonService.unlock(redissonClusterLock);
                if (e instanceof AppException){
                    // 交给全局或异常处理
                    throw new AppException(e.getMessage());
                }
                return Mono.just(BaseResponse.LogBackError(e.getMessage(), log));
            }
            // key统一格式：post_publish_key:snowflakeId（注意是snowflakeId不是userAccount或者userName）
        }
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
        return Mono.just(BaseResponse.getResponseEntitySuccess("修改成功"));
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
        // 获取分布式锁
        RedissonClusterLock redissonClusterLock = new RedissonClusterLock(
                String.valueOf(userDo.getId()),
                PostConstant.Post_CONTROLLER + PostConstant.POST_UPDATE_ALL,
                PostConstant.POST_CHANGE_KEY_EXPIRE_TIME
        );
        if (!redissonService.tryLock(redissonClusterLock)){
            String warningMessage = String.format("用户正在修改帖子，请稍后再试，account: %s", request.getSenderId());
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        // try-catch优先级高于全局异常
        try {
            PostAo postAo = postConverter.updateRequestToAo(request, userDo.getId());
            postService.updatePostFirst(postAo, request.getPostId());
        } catch (Exception e){
            // 出现任何异常都直接解除分布式锁
            redissonService.unlock(redissonClusterLock);
            // 如果是App异常，就抛出交给全局异常处理器，然后交给前端
            if (e instanceof AppException){
                throw new AppException(e.getMessage());
            }
        }

        return Mono.just(BaseResponse.getResponseEntitySuccess("修改申请已提交，请等待"));
    }

    /**
     * 预览postList
     * 查询postList,只包含list的基本信息不包括内容content
     * 响应体应该包含：postInfo，post-fileIds，postDetails
     * 通过list<postId>查询post消息;
     * FileId拿到之后直接去oss服务将fileIdList转为url
     * 调用：/oss/getFileUrlByFileIds
     * @param request   List<Long> postIds
     * @return      List<PostInfoAo> postInfoAos;
     */
    @Deprecated
    @PostMapping("/getPostInfoList/deprecated")
    public Mono<BaseResponse<GetPostInfoListResponse>>
    getPosts(@Valid @RequestBody GetPostInfoListRequest request){
        List<Long> postIds = request.getPostIds();
        if (CollectionUtils.isEmpty(postIds)){
            return Mono.just(BaseResponse.LogBackError("参数错误", log));
        }
        List<PostInfoAo> postAoList = postSearchService.findPostInfoList(postIds);
        GetPostInfoListResponse getPostResponse = new GetPostInfoListResponse();
        getPostResponse.setPostInfoAos(postAoList);
        return Mono.just(BaseResponse.getResponseEntitySuccess(getPostResponse));
    }

    @PostMapping("/getPostInfoList")
    public Mono<BaseResponse<GetPostPreviewListResponse>>
    getPostsNew(@Valid @RequestBody GetPostPreviewListRequest request){
        List<Long> postIds = request.getPostIds();
        if (CollectionUtils.isEmpty(postIds)){
            return Mono.just(BaseResponse.LogBackError("参数错误", log));
        }
        List<PostInfoAo> postAoList = postSearchService.findPostInfoList(postIds);

        if (CollectionUtils.isEmpty(postAoList)){
            return Mono.just(BaseResponse.getResponseEntitySuccess(new GetPostPreviewListResponse()));
        }

        List<PostPreviewVo> postPreviewVos = postFrontService.toPostPreviewVoList(postAoList);

        GetPostPreviewListResponse response = new GetPostPreviewListResponse();
        response.setPostPreviewVos(postPreviewVos);

        return Mono.just(BaseResponse.getResponseEntitySuccess(response));
    }

    // 如何从各种数据查询List<postId>的逻辑在postSearchService中
    // get Post
    @Deprecated
    @GetMapping("/getPost/deprecated")
    public Mono<BaseResponse<GetPostResponse>>
    getPost(@RequestParam Long postId,
            @RequestParam Integer pageNum){
        if (ObjectUtils.isEmpty(postId)){
            return Mono.just(BaseResponse.LogBackError("参数错误", log));
        }
        PostAo postAo = postService.findPostById(postId);
        if (postAo == null){
            String warningMessage = String.format("帖子不存在，postId: %s", postId);
            return Mono.just(BaseResponse.LogBackError(warningMessage, log));
        }
        if (ObjectUtils.isEmpty(pageNum) || pageNum < 1){
            pageNum = 1;
        }
        List<PostCommentDo> postCommentList = postCommentService.getLevel1PostComments(postId, PostConstant.COMMENT_PAGE_SIZE, pageNum);
        GetPostResponse getPostResponse = new GetPostResponse();
        getPostResponse.setPostAo(postAo);
        getPostResponse.setPostCommentList(postCommentList);
        return Mono.just(BaseResponse.getResponseEntitySuccess(getPostResponse));
    }

    @GetMapping("/getPost")
    public BaseResponse<SinglePostResponse>
    getPostNew(@RequestParam Long postId,
            @RequestParam Integer pageNum){
        if (ObjectUtils.isEmpty(postId)){
            return BaseResponse.LogBackError("参数错误", log);
        }

        PostAo postAo = postService.findPostById(postId);
        if (postAo == null || postAo.getId() == null){
            String warningMessage = String.format("帖子不存在，postId: %s", postId);
            return BaseResponse.LogBackError(warningMessage, log);
        }
        if (ObjectUtils.isEmpty(pageNum) || pageNum < 1){
            pageNum = 1;
        }
        List<PostCommentDo> postCommentList = postCommentService.getLevel1PostComments(postId, PostConstant.COMMENT_PAGE_SIZE, pageNum);

        SinglePostResponse singlePostResponse = new SinglePostResponse();
        // 转换为vo
        PostVo postVo = postFrontService.postAoToPostVo(postAo);
        List<CommentVo> commentVos = postFrontService.getCommentVosByPostCommentDos(postCommentList);
        singlePostResponse.postVo = postVo;
        singlePostResponse.commentVos = commentVos;
        return BaseResponse.getResponseEntitySuccess(singlePostResponse);
    }

    // 获取下拉一级评论（pageNum）
    @GetMapping("/getPostLevel1Comments")
    public Mono<BaseResponse<GetPostCommentsResponse>>
    getPostLevel1Comments(@RequestParam Long postId,
                    @RequestParam Integer pageSize,
                    @RequestParam Integer pageNum){
        return getPostComments(postId, null, pageSize, pageNum);
    }

    // 获取二级评论（commentId + pageNum）
    @GetMapping("/getPostLevel2Comments")
    public Mono<BaseResponse<GetPostCommentsResponse>>
    getPostLevel2Comments(@RequestParam Long postId,
                    @RequestParam Long leve1commentId,
                    @RequestParam Integer pageSize,
                    @RequestParam Integer pageNum){
        return getPostComments(postId, leve1commentId, pageSize, pageNum);
    }

    private Mono<BaseResponse<GetPostCommentsResponse>> getPostComments(
            Long postId,
            Long leve1commentId,
            Integer pageSize,
            Integer pageNum) {
        if (postId == null) {
            return Mono.just(BaseResponse.LogBackError("参数错误", log));
        }

        if (leve1commentId != null) {
            PostCommentDo postCommentDo = postCommentService.getPostCommentById(leve1commentId);
            if (postCommentDo == null) {
                String warningMessage = String.format("评论不存在，commentId: %s", leve1commentId);
                return Mono.just(BaseResponse.LogBackError(warningMessage, log));
            }
        }

        List<PostCommentDo> postCommentList;
        // 没有level1Id默认为他就是level1评论
        if (leve1commentId == null) {
            postCommentList = postCommentService.getLevel1PostComments(postId, pageSize, pageNum);
        } else {
            postCommentList = postCommentService.getLevel2PostComments(postId, leve1commentId, pageSize, pageNum);
        }

        List<PostCommentAo> postCommentAoList = postCommentService.getPostCommentAoList(postCommentList);
        GetPostCommentsResponse getPostCommentsResponse = new GetPostCommentsResponse();

        if (!CollectionUtils.isEmpty(postCommentAoList)) {
            List<Long> fileIds = postCommentAoList.stream()
                    .map(postCommentAo -> postCommentAo.getCommenterAvatarFileId() == null ? null : postCommentAo.getCommenterAvatarFileId())
                    .collect(Collectors.toList());
            List<String> fileUrls = ossService.getFileUrlsByFileIds(fileIds);

            List<PostCommentDto> postCommentDtoList = new ArrayList<>();
            for (int i = 0; i < postCommentAoList.size(); i++) {
                PostCommentDto dto = postCommentConverter.postCommentAoToPostCommentDto(postCommentAoList.get(i), fileUrls.get(i));
                postCommentDtoList.add(dto);
            }
            getPostCommentsResponse.setPostCommentDtosList(postCommentDtoList);
        }

        return Mono.just(BaseResponse.getResponseEntitySuccess(getPostCommentsResponse));
    }
}
