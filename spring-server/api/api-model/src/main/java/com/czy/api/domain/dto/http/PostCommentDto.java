package com.czy.api.domain.dto.http;

import lombok.Data;


@Data
public class PostCommentDto {
    // 所属帖子id；not null （索引）
    private Long postId;
    // 评论者id；not null   （索引）
    private Long commenterId;
    // 评论者账号；not null
    private String commenterAccount;
    // 评论者昵称；not null
    private String commenterName;
    // 评论者头像文件id；not null
    // 注意此字段在查询的时候是fileId，需要调用oss将id转为url
    private String commenterAvatarFileId;
    // 此评论回复的评论id（索引）；null able（null就是直接回复帖子；是一级评论）
    private Long replyCommentId = null;
    // 评论内容；not null
    private String content;
    // 评论时间；not null
    private Long timestamp = System.currentTimeMillis();
}
