package com.czy.post.mapper.service;

import com.czy.api.domain.ao.post.PostAo;

/**
 * @author 13225
 * @date 2025/4/18 21:10
 */
public interface PostTransactionService {
    // es + mongo的事务
    void storePostToDatabase(PostAo postAo, Long id);

    void deletePostContentById(Long id);

    void updatePostContentToDatabase(PostAo postAo, Long postId);
}
