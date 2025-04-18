package com.czy.post.mapper.service;

import com.czy.api.domain.ao.post.PostAo;

/**
 * @author 13225
 * @date 2025/4/18 18:29
 */
public interface PostStorageService {
    // es + mongo
    void storePostContentToDatabase(PostAo postAo, Long id);
    // mysql
    void storePostInfoToDatabase(PostAo postAo, Long id);
}
