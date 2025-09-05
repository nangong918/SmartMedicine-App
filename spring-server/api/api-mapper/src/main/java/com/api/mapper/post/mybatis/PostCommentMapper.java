package com.api.mapper.post.mybatis;

import com.czy.api.domain.Do.post.comment.PostCommentDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/5 17:50
 */
@Mapper
public interface PostCommentMapper {


    /// 查询
    /**
     * 获取帖子一级评论 (时间排序)
     * @param postId    帖子id
     * @param offset    偏移量
     * @param size      每页数量
     * @return 帖子一级评论
    SELECT *
    FROM post_comment
    WHERE post_id = #{postId} AND reply_comment_id IS NULL
    ORDER BY timestamp DESC
    LIMIT #{size} OFFSET #{offset};
     */
    List<PostCommentDo> getPostLevel1Comment(
            @Param("postId") Long postId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 获取帖子一级评论 (时间排序)
     * @param postId    帖子id
     * @return 帖子2级评论
    SELECT *
    FROM post_comment
    WHERE post_id = #{postId} AND reply_comment_id = #{replyCommentId}
    ORDER BY timestamp DESC
    LIMIT #{size} OFFSET #{offset};
     */
    List<PostCommentDo> getCommentLevel2ByPostIdAndReplyCommentId(
            @Param("postId") Long postId,
            @Param("replyCommentId") Long replyCommentId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /// 增
    int insertLevel1Comment(
            @Param("id") Long id,
            @Param("postId") Long postId,
            @Param("commenterId") Long commenterId,
            @Param("timestamp") Long timestamp,
            @Param("content") String content
    );

    int insertLevel2Comment(
            @Param("id") Long id,
            @Param("postId") Long postId,
            @Param("commenterId") Long commenterId,
            @Param("replyCommentId") Long replyCommentId,
            @Param("timestamp") Long timestamp,
            @Param("content") String content
    );

    int insert(PostCommentDo postCommentDo);
    int insertBatch(@Param("list") List<PostCommentDo> postCommentDos);

    /// 删
    void deleteById(Long id);
    void deleteBatch(@Param("list") List<Long> idList);
}
