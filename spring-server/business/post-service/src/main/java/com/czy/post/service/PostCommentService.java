package com.czy.post.service;

import com.czy.api.domain.Do.post.comment.PostCommentDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 11:51
 */
public interface PostCommentService {

    // 第二次获取的非实时性需要考虑
    // 分页获取某个post的一级comment （一页多少条n + 第几页m）（comment在mongodb需要用Page）
    // TODO 暂时根据时间排序，后续需要根据综合算法如点赞数排序（类似推荐系统）
    List<PostCommentDo> getLevel1PostComments(Long postId, Integer pageSize, Integer pageNum);

    // 获取某个comment的子评论（一页多少条n + 第几页m）（comment在mongodb需要用Page）
    List<PostCommentDo> getLevel2PostComments(Long postId, Long level2CommentId, Integer pageSize, Integer pageNum);
}
