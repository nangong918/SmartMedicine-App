package com.czy.post.mapper.service;

import com.czy.api.domain.ao.post.PostAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 18:29
 */
public interface PostStorageService {
    // es + mongo
    void storePostContentToDatabase(PostAo postAo, Long id);
    // mysql
    void storePostInfoToDatabase(PostAo postAo, Long id);

    // 删除es + mongo
    void deletePostContentFromDatabase(Long id);
    // 删除mysql
    void deletePostInfoFromDatabase(Long id);
    
    // 获取PostAo
    PostAo findPostAoById(Long id);

    List<PostAo> findPostAoByIds(List<Long> idList);

    void updatePostContentToDatabase(PostAo postAo, Long postId);

    void updatePostInfoToDatabase(PostAo postAo, Long postId);
}
