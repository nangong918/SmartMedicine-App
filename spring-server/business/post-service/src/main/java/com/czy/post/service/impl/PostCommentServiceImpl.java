package com.czy.post.service.impl;

import cn.hutool.core.util.IdUtil;
import com.api.mapper.post.mongo.PostCommentMongoMapper;
import com.api.mapper.post.mybatis.PostCommentMapper;
import com.api.mapper.post.mybatis.PostInfoMapper;
import com.api.mapper.post.mybatis.bo.PostCommentBoMapper;
import com.czy.api.api.user.user.UserService;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.CommentConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.post.comment.PostCommentMongoDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.CommentAo;
import com.czy.api.domain.ao.post.PostCommentAo;
import com.czy.api.domain.bo.post.CommentBo;
import com.czy.api.domain.dto.service.CommentResultDto;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.PostExceptions;
import com.czy.api.exception.UserExceptions;
import com.czy.post.service.PostCommentService;
import com.utils.minio.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/4/24 11:59
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentMongoMapper postCommentMongoMapper;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private UserService userService;
    private final PostInfoMapper postInfoMapper;
    private final PostCommentMapper postCommentMapper;
    private final PostCommentBoMapper postCommentBoMapper;
    private final CommentConverter commentConverter;
    private final OssService ossService;

    @Override
    public List<CommentAo> getLevel1PostCommentAos(Long postId, Integer pageSize, Integer pageNum) {
        int finalPageNum = pageNum;
        if (finalPageNum < PostConstant.COMMENT_MIN_PAGE_SIZE){
            finalPageNum = PostConstant.COMMENT_MIN_PAGE_SIZE;
        }
        else if (finalPageNum > PostConstant.COMMENT_MAX_PAGE_SIZE){
            finalPageNum = PostConstant.COMMENT_MAX_PAGE_SIZE;
        }

        // 计算偏移量
        int offset = (finalPageNum - 1) * pageSize;

        List<PostCommentDo> postCommentDoList = postCommentMapper.getPostLevel1Comment(
                postId,
                offset,
                PostConstant.COMMENT_PAGE_SIZE
        );

        if (CollectionUtils.isEmpty(postCommentDoList)){
            return new ArrayList<>();
        }

        List<Long> commentIds = postCommentDoList.stream()
                .filter(Objects::nonNull)
                .map(PostCommentDo::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(commentIds)){
            return new ArrayList<>();
        }

        List<CommentBo> commentBos = postCommentBoMapper.getCommentBoByIdList(
                commentIds
        );
        if (CollectionUtils.isEmpty(commentBos)){
            return new ArrayList<>();
        }

        // oss
        List<Long> ossFileIds = commentBos.stream()
                .map(CommentBo::getAvatarFileId)
                .collect(Collectors.toList());
        List<String> urls = ossService.getFileUrlsByFileIds(ossFileIds);

        // ao
        List<CommentAo> commentAos = new ArrayList<>(commentBos.size());
        for (int i = 0; i < commentBos.size(); i++){
            CommentAo commentAo = commentConverter.getAoByBo(
                    commentBos.get(i),
                    urls.get(i)
            );
            commentAos.add(commentAo);
        }

        return commentAos;
    }

    @Override
    public List<CommentAo> getLevel2PostCommentAos(Long postId, Long level2CommentId, Integer pageSize, Integer pageNum) {
        int finalPageNum = pageNum;
        if (finalPageNum < PostConstant.COMMENT_MIN_PAGE_SIZE){
            finalPageNum = PostConstant.COMMENT_MIN_PAGE_SIZE;
        }
        else if (finalPageNum > PostConstant.COMMENT_MAX_PAGE_SIZE){
            finalPageNum = PostConstant.COMMENT_MAX_PAGE_SIZE;
        }

        // 计算偏移量
        int offset = (finalPageNum - 1) * pageSize;

        List<PostCommentDo> postCommentDoList = postCommentMapper.getCommentLevel2ByPostIdAndReplyCommentId(
                postId,
                level2CommentId,
                offset,
                PostConstant.COMMENT_PAGE_SIZE
        );

        if (CollectionUtils.isEmpty(postCommentDoList)){
            return new ArrayList<>();
        }

        List<Long> commentIds = postCommentDoList.stream()
                .filter(Objects::nonNull)
                .map(PostCommentDo::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(commentIds)){
            return new ArrayList<>();
        }

        List<CommentBo> commentBos = postCommentBoMapper.getCommentBoByIdList(
                commentIds
        );
        if (CollectionUtils.isEmpty(commentBos)){
            return new ArrayList<>();
        }

        // oss
        List<Long> ossFileIds = commentBos.stream()
                .map(CommentBo::getAvatarFileId)
                .collect(Collectors.toList());
        List<String> urls = ossService.getFileUrlsByFileIds(ossFileIds);

        // ao
        List<CommentAo> commentAos = new ArrayList<>(commentBos.size());
        for (int i = 0; i < commentBos.size(); i++){
            CommentAo commentAo = commentConverter.getAoByBo(
                    commentBos.get(i),
                    urls.get(i)
            );
            commentAos.add(commentAo);
        }

        return commentAos;
    }

    @Override
    public PostCommentMongoDo getPostCommentById(Long commentId) {
        return postCommentMongoMapper.findCommentById(commentId);
    }

    @Override
    public List<PostCommentAo> getPostCommentAoList(List<PostCommentMongoDo> postCommentDoList) {
        if (CollectionUtils.isEmpty(postCommentDoList)){
            return new ArrayList<>();
        }
        // null也需要保存，保证list长度一致
        // 保留null以确保列表长度一致
        List<Long> commenterIds = postCommentDoList.stream()
                .map(postComment -> postComment == null ? null : postComment.getCommenterId())
                .collect(Collectors.toList());

        // 可能会去除null对象，为了position一一对应，需要适用
        List<UserDo> userDos = userService.getByUserIds(commenterIds);
        // 转换为 Map<Integer, UserDo> 避免双重for循环的O(n*n),而是O(m+n)
        // key是userId也就是Map<UserId,UserDo> userMap
        Map<Long, UserDo> userMap = userDos.stream()
                .collect(Collectors.toMap(UserDo::getId, user -> user, (existing, replacement) -> existing));
        List<UserDo> allUserDos = new ArrayList<>(commenterIds.size());

        // 生成存在null的allUserDos
        for(int i = 0; i < postCommentDoList.size(); i++){
            if (commenterIds.get(i) != null && userMap.get(commenterIds.get(i)) != null){
                allUserDos.add(userMap.get(commenterIds.get(i)));
            }
            else {
                allUserDos.add(null);
            }
        }

        // 生成一个存在null的list
        List<PostCommentAo> postCommentAoList = new ArrayList<>();
        for (int i = 0; i < postCommentDoList.size(); i++) {
            PostCommentMongoDo postCommentDo = postCommentDoList.get(i);
            if (postCommentDo == null){
                postCommentAoList.add(null);
                continue;
            }
            PostCommentAo ao = new PostCommentAo();
            ao.setPostId(postCommentDo.getPostId());
            ao.setCommenterId(postCommentDo.getCommenterId());
            ao.setContent(postCommentDo.getContent());
            ao.setTimestamp(postCommentDo.getTimestamp());

            if (allUserDos.get(i) != null){
                ao.setCommenterAccount(allUserDos.get(i).getAccount());
                ao.setCommenterName(allUserDos.get(i).getUserName());
                ao.setCommenterAvatarFileId(allUserDos.get(i).getAvatarFileId());
                ao.setReplyCommentId(postCommentDo.getReplyCommentId());
            }
            postCommentAoList.add(ao);
        }
        return postCommentAoList;
    }

    @NotNull
    @Override
    public CommentResultDto comment(Long senderId, Long postId, @Nullable Long replyCommentId, @NotNull String content, Long timestamp) {
        CommentResultDto resultDto = new CommentResultDto();

        // 参数校验
        if (senderId == null || postId == null) {
            resultDto.setSuccess(false);
            resultDto.setExceptionEnums(CommonExceptions.PARAM_ERROR);
            return resultDto;
        }
        UserDo sender = userService.getUserById(senderId);
        if (sender == null || sender.getId() == null){
            resultDto.setSuccess(false);
            resultDto.setExceptionEnums(UserExceptions.USER_NOT_EXIST);
            return resultDto;
        }
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        if (postInfoDo == null || postInfoDo.getId() == null){
            resultDto.setSuccess(false);
            resultDto.setExceptionEnums(PostExceptions.POST_NOT_EXIST);
            return resultDto;
        }

        // 添加到mongodb数据库中
        PostCommentMongoDo postCommentDo = new PostCommentMongoDo();
        postCommentDo.setId(IdUtil.getSnowflakeNextId());
        postCommentDo.setPostId(postId);
        postCommentDo.setCommenterId(senderId);
        postCommentDo.setReplyCommentId(replyCommentId);
        postCommentDo.setContent(content);
        postCommentDo.setTimestamp(timestamp);
        postCommentMongoMapper.saveComment(postCommentDo);

        return resultDto;
    }

    @Override
    public void deleteComment(Long postId, Long commentId, Long senderId) {
        postCommentMongoMapper.deleteCommentByPostIdAndCommentId(postId, commentId);
    }
}
