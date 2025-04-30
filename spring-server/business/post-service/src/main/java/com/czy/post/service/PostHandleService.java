package com.czy.post.service;

import com.czy.api.domain.Do.post.collect.PostCollectDo;
import com.czy.api.domain.Do.post.collect.PostCollectFolderDo;
import com.czy.api.domain.Do.post.comment.PostCommentDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/23 17:00
 * Post处理service，用于处理 评论，点赞，转发，收藏
 */
public interface PostHandleService {
    // 评论
    // 发布评论
    void postComment(PostCommentDo postCommentDo);
    // 删除评论
    void deleteComment(Long postId, Long commentId);

    // 点赞（这不是直播的点赞，是post的点赞，一个用户只能点击一次，无需存入redis）
    void postLike(Long postId, Long userId);
    // 取消点赞
    void deletePostLike(Long postId, Long userId);

    // 转发（聊天记录 + netty）
    void postForward(Long postId);

    // 收藏（直接插入数据库）
    void postCollect(Long postId, Long collectFolderId);
    // 取消收藏
    void deletePostCollect(Long postId, Long collectFolderId);
    // 修改收藏
    void postCollectUpdate(Long postId, Long folderId, Long newFolderId);
    // 创建收藏夹
    Long createPostCollectFolder(Long userId, String collectFolderName);
    // 删除收藏夹
    void deletePostCollectFolder(Long collectFolderId, Long userId);
    // 修改收藏夹
    void updatePostCollectFolder(Long collectFolderId, Long userId, String newCollectFolderName);
    // 查询某个user的全部文件夹
    List<PostCollectFolderDo> findPostCollectFolderByUserId(Long userId);
    // 查询某个文件夹下的全部帖子
    List<PostCollectDo> findPostCollectsByCollectFolderId(Long collectFolderId);
}
