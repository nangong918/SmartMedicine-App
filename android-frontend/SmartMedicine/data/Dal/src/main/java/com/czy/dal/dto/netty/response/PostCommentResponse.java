package com.czy.dal.dto.netty.response;


import com.czy.dal.dto.netty.base.NettyOptionResponse;


/**
 * @author 13225
 * @date 2025/4/28 17:27
 */
public class PostCommentResponse extends NettyOptionResponse {
    // postId
    public Long postId;
    // 评论id (删除评论的时候用)
    public Long commentId;
    // 评论内容
    public String content;
    // 此评论回复的评论id（索引）；null able（null就是直接回复帖子；是一级评论）
    public Long replyCommentId = null;
    // commenterId ;就是senderId查询为id
}
