package com.czy.post.service;

import com.czy.api.domain.ao.post.PostAo;

/**
 * @author 13225
 * @date 2025/4/18 21:10
 */
public interface PostTransactionService {
    // es + mongo的事务
    void storePostToDatabase(PostAo postAo);

    void deletePostContentById(Long id);

    void updatePostContentToDatabase(PostAo postAo);
}
