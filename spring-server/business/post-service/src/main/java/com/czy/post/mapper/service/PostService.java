package com.czy.post.mapper.service;

import com.czy.api.domain.ao.post.PostAo;
import lombok.NonNull;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 17:20
 */
public interface PostService {

    // 发布
    // 初次上传到redis
    Long releasePostFirst(@NonNull PostAo postAo);
    // oss的成功的二次上传
    boolean releasePostAfterOss(@NonNull Long publishId);

    // 删除
    boolean deletePost(Long postId);

    // 更改
    boolean updatePost(PostAo postAo);

    // 查询
    PostAo findPostById(Long postId);
    List<PostAo> findPostsByIdList(List<Long> idList);

}
