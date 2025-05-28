package com.czy.post.service;

import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.ao.post.PostAo;
import com.czy.api.domain.vo.CommentVo;
import com.czy.api.domain.vo.PostVo;
import lombok.NonNull;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 17:20
 */
public interface PostService {

    // 发布
    // 不需要上传文件的情况
    long releasePostWithoutFile(@NonNull PostAo postAo);
    // 初次上传到redis
    long releasePostFirst(@NonNull PostAo postAo) throws Exception;
    // oss的成功的二次上传
    void releasePostAfterOss(@NonNull PostAo postAo);
    // 验证是否为合法的发布
    boolean isLegalPost(PostAo postAo);

    /**
     * 删除
     * @param postId    publishId;也就是雪花id
     * @param userId    用户id
     */
    void deletePost(Long postId, Long userId);

    // 更改完全
    void updatePostFirst(PostAo postAo, Long postId) throws Exception;
    void updatePostAfterOss(PostAo postAo);

    // 局部更改
    void updatePostInfoAndContent(PostAo postAo);
    void updatePostInfo(PostAo postAo);

    // 查询
    PostAo findPostById(Long postId);
    List<PostAo> findPostsByIdList(List<Long> idList);

    // 转换
    // PostAo -> PostVo
    PostVo postAoToPostVo(PostAo postAo);
    // List<PostCommentDo> -> List<CommentVo>
    List<CommentVo> getCommentVosByPostCommentDos(List<PostCommentDo> postCommentDos);
}
