package com.czy.dal.dto.netty.request;


import com.czy.dal.constant.netty.RequestMessageType;
import com.czy.dal.dto.netty.base.NettyOptionRequest;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 评论帖子
 */

public class PostCommentRequest extends NettyOptionRequest {

    // postId
    public Long postId;
    // 评论id (删除评论的时候用)
    public Long commentId;
    // 评论内容
    public String content;
    // 此评论回复的评论id（索引）；null able（null就是直接回复帖子；是一级评论）
    public Long replyCommentId = null;
    // commenterId ;就是senderId查询为id

    public PostCommentRequest(Long postId, Integer optionCode){
        super(optionCode);
        super.setType(RequestMessageType.Post.COMMENT_POST);
        this.postId = postId;
    }
}
