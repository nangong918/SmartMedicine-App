package com.czy.api.domain.dto.socket.request;

import com.czy.api.constant.netty.RequestMessageType;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/23 11:00
 * 评论帖子
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostCommentRequest extends BaseRequestData {

    // postId
    public Long postId;
    // 评论内容
    public String content;
    // 此评论回复的评论id（索引）；null able（null就是直接回复帖子；是一级评论）
    public Long replyCommentId = null;
    // commenterId ;就是senderId查询为id

    public PostCommentRequest(Long postId){
        super.setType(RequestMessageType.Post.COMMENT_POST);
        this.postId = postId;
    }
}
