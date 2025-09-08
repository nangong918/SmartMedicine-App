package com.czy.post.service.impl;

import cn.hutool.core.util.IdUtil;
import com.api.mapper.post.mybatis.PostCommentMapper;
import com.api.mapper.post.mybatis.PostInfoMapper;
import com.api.mapper.post.mybatis.bo.PostCommentBoMapper;
import com.czy.api.api.user.user.UserService;
import com.czy.api.constant.post.PostConstant;
import com.czy.api.converter.domain.post.CommentConverter;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.api.domain.Do.user.UserDo;
import com.czy.api.domain.ao.post.CommentAo;
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
    public List<CommentAo> getLevel2PostCommentAos(Long postId, Long replyCommentId, Integer pageSize, Integer pageNum) {
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
                replyCommentId,
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
    public PostCommentDo getPostCommentById(Long commentId) {
        return postCommentMapper.getPostCommentById(commentId);
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

        Long newCommentId = IdUtil.getSnowflakeNextId();
        int result;
        if (replyCommentId != null){
            result = postCommentMapper.insertLevel2Comment(
                    newCommentId,
                    postId,
                    senderId,
                    replyCommentId,
                    timestamp,
                    content
            );
        }
        else {
            result = postCommentMapper.insertLevel1Comment(
                    newCommentId,
                    postId,
                    senderId,
                    timestamp,
                    content
            );
        }
        if (result <= 0){
            resultDto.setSuccess(false);
            resultDto.setExceptionEnums(PostExceptions.COMMENT_INSERT_TO_DB_FAILED);
            return resultDto;
        }
        resultDto.setCommentId(newCommentId);

        return resultDto;
    }

    @Override
    public void deleteComment(Long postId, Long commentId, Long senderId) {
        postCommentMapper.deleteById(commentId);
    }
}
