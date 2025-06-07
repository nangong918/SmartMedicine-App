package com.czy.api.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVo implements Serializable {
    // comment 所属相关
    public Long commentId = null;
    public Long replyCommentId = null;
    public Long postId = null;

    // comment 内容相关
    public String content;
    public Long commentTimestamp;

    // commend-user 相关
    public Long commenterId;
    public String commentName;
    public String commentAvatarUrl;
}
