package com.czy.api.domain.Do.post.comment;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/9/5 17:44
 * 评论的关系，存在mysql post_comment
 */
@Data
public class PostCommentDo {
    @Id
    // id；not null
    private Long id;
    // 所属帖子id；not null （索引）
    private Long postId;
    // 评论者id；not null   （索引）
    private Long commenterId;
    // 此评论回复的评论id（索引）；null able（null就是直接回复帖子；是一级评论）
    private Long replyCommentId = null;
    // 评论时间；not null
    private Long timestamp = System.currentTimeMillis();
    // 评论内容；not null (哎，懒得去写mongoDb代码了，反正comment内容和post内容不一样，不是json类型的内容)
    private String content;
}
