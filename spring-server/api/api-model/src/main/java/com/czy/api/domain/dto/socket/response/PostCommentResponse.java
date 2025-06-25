package com.czy.api.domain.dto.socket.response;

import com.czy.api.domain.dto.base.NettyOptionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/4/28 17:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
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
    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("postId", String.valueOf(postId));
        map.put("commentId", String.valueOf(commentId));
        map.put("content", content);
        if (replyCommentId != null) {
            map.put("replyCommentId", String.valueOf(replyCommentId));
        }
        return map;
    }
}
