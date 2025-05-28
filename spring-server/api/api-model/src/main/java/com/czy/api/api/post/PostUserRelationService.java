package com.czy.api.api.post;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/28 9:31
 * 1. 记录、查询 post的作者是谁
 * 2. 记录、查询 user发表的全部post
 * 3. 记录、查询 user看过哪些post
 */
public interface PostUserRelationService {

    // 查询 post 的作者是谁（user发表post）
    Long queryPostAuthor(Long postId);

    // 查询 user 发表的全部post
    List<Long> queryUserPosts(Long userId);

    // 记录user看过的postIds（只记录看没看过，分值没法立马算出来，所以需要跑离线特征）
    void recordUserBrowsePost(Long userId, List<Long> postIds);

    // 查询user看过的postIds（只是看没看过）
    List<Long> queryUserBrowsePost(Long userId);

    // 查询帖子被哪些人看过
    List<Long> queryPostBrowseUser(Long postId);

}
