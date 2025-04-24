package com.czy.post.service.impl;

import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.post.mapper.mongo.PostCommentMongoMapper;
import com.czy.post.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 11:59
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentMongoMapper postCommentMongoMapper;

    @Override
    public List<PostCommentDo> getLevel1PostComments(Long postId, Integer pageSize, Integer pageNum) {
        return postCommentMongoMapper.findLevel1CommentsByPostIdAndPaging(postId, pageSize, pageNum);
    }

    @Override
    public List<PostCommentDo> getLevel2PostComments(Long postId, Long level2CommentId, Integer pageSize, Integer pageNum) {
        return postCommentMongoMapper.findLevel2CommentsByPostIdAndReplyCommentIdPaging(postId, level2CommentId, pageSize, pageNum);
    }
}
