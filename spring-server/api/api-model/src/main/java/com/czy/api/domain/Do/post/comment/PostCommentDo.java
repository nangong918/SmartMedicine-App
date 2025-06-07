package com.czy.api.domain.Do.post.comment;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/4/16 21:31
 * 存放在mongoDB中，评论就没必要存储在ES中了
 */
@org.springframework.data.mongodb.core.mapping.Document("post_comment")
@Data
public class PostCommentDo implements Serializable {
    @Id
    // id；not null
    private Long id;
    // 所属帖子id；not null （索引）
    private Long postId;
    // 评论者id；not null   （索引）
    private Long commenterId;
    // 此评论回复的评论id（索引）；null able（null就是直接回复帖子；是一级评论）
    private Long replyCommentId = null;
    // 评论内容；not null
    private String content;
    // 评论时间；not null
    private Long timestamp = System.currentTimeMillis();
}
