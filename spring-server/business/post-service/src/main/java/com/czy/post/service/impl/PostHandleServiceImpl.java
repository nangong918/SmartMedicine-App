package com.czy.post.service.impl;

import com.czy.api.domain.Do.post.collect.PostCollectDo;
import com.czy.api.domain.Do.post.collect.PostCollectFolderDo;
import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.Do.post.post.PostInfoDo;
import com.czy.post.mapper.mongo.PostCommentMongoMapper;
import com.czy.post.mapper.mysql.PostCollectFolderMapper;
import com.czy.post.mapper.mysql.PostCollectMapper;
import com.czy.post.mapper.mysql.PostInfoMapper;
import com.czy.post.service.PostHandleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/23 17:52
 * Transactional事务保证操作原子性
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostHandleServiceImpl implements PostHandleService {

    private final PostCollectMapper postCollectMapper;
    private final PostCollectFolderMapper postCollectFolderMapper;
    private final PostCommentMongoMapper postCommentMongoMapper;
    private final PostInfoMapper postInfoMapper;

    @Transactional
    @Override
    public void postComment(PostCommentDo postCommentDo) {
        // mongo存储文本
        postCommentMongoMapper.saveComment(postCommentDo);
        // mysql存储次数
        Long postId = postCommentDo.getPostId();
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        postInfoDo.setCommentCount(postInfoDo.getCommentCount() + 1);
        postInfoMapper.updatePostInfoDo(postInfoDo);
    }

    @Transactional
    @Override
    public void deleteComment(Long postId, Long commentId) {
        // mongo删除
        postCommentMongoMapper.deleteCommentById(commentId);
        // mysql删除次数
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        postInfoDo.setCommentCount(postInfoDo.getCommentCount() - 1);
        postInfoMapper.updatePostInfoDo(postInfoDo);
    }

    @Transactional
    @Override
    public void postLike(Long postId, Long userId) {
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        postInfoDo.setLikeCount(postInfoDo.getLikeCount() + 1);
        postInfoMapper.updatePostInfoDo(postInfoDo);
    }

    @Transactional
    @Override
    public void deletePostLike(Long postId, Long userId) {
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        postInfoDo.setLikeCount(postInfoDo.getLikeCount() - 1);
        postInfoMapper.updatePostInfoDo(postInfoDo);
    }

    @Transactional
    @Override
    public void postForward(Long postId) {
        PostInfoDo postInfoDo = postInfoMapper.getPostInfoDoById(postId);
        postInfoDo.setForwardCount(postInfoDo.getForwardCount() + 1);
        postInfoMapper.updatePostInfoDo(postInfoDo);
    }

    @Override
    public void postCollect(Long postId, Long collectFolderId) {
        PostCollectDo postCollectDo = new PostCollectDo();
        postCollectDo.setPostId(postId);
        postCollectDo.setCollectFolderId(collectFolderId);
        postCollectMapper.savePostCollect(postCollectDo);
    }

    @Override
    public void deletePostCollect(Long postId, Long collectFolderId) {
        postCollectMapper.deletePostCollectByPostIdAndCollectFolderId(postId, collectFolderId);
    }

    @Override
    public void postCollectUpdate(Long postId, Long folderId, Long newFolderId) {
        PostCollectDo postCollectDo = postCollectMapper.findPostCollectByPostIdAndFolderId(postId, folderId);
        if (postCollectDo == null || postCollectDo.getPostId() == null){
            postCollectDo = new PostCollectDo();
            postCollectDo.setPostId(postId);
            postCollectDo.setCollectFolderId(newFolderId);
            postCollectMapper.savePostCollect(postCollectDo);
        }
        else {
            postCollectDo.setPostId(postId);
            postCollectDo.setCollectFolderId(newFolderId);
            postCollectMapper.updatePostCollect(postCollectDo);
        }
    }

    @Override
    public Long createPostCollectFolder(Long userId, String collectFolderName) {
        PostCollectFolderDo postCollectFolderDo = new PostCollectFolderDo();
        postCollectFolderDo.setUserId(userId);
        postCollectFolderDo.setCollectFolderName(collectFolderName);
        return postCollectFolderMapper.savePostCollectFolder(postCollectFolderDo);
    }

    @Override
    public void deletePostCollectFolder(Long collectFolderId, Long userId) {
        postCollectFolderMapper.deletePostCollectFolder(collectFolderId, userId);
    }

    @Override
    public void updatePostCollectFolder(Long collectFolderId, Long userId, String newCollectFolderName) {
        PostCollectFolderDo postCollectFolderDo = postCollectFolderMapper.findPostCollectFolderById(collectFolderId);
        if (postCollectFolderDo == null || postCollectFolderDo.getUserId() == null){
            postCollectFolderDo = new PostCollectFolderDo();
            postCollectFolderDo.setUserId(userId);
            postCollectFolderDo.setCollectFolderName(newCollectFolderName);
            postCollectFolderMapper.savePostCollectFolder(postCollectFolderDo);
        }
        else {
            postCollectFolderDo.setUserId(userId);
            postCollectFolderDo.setCollectFolderName(newCollectFolderName);
            postCollectFolderMapper.updatePostCollectFolder(postCollectFolderDo);
        }
    }

    @Override
    public List<PostCollectFolderDo> findPostCollectFolderByUserId(Long userId) {
        return postCollectFolderMapper.findPostCollectFolderByUserId(userId);
    }

    @Override
    public List<PostCollectDo> findPostCollectsByCollectFolderId(Long collectFolderId) {
        return postCollectMapper.findPostCollectsByCollectFolderId(collectFolderId);
    }
}
