package com.czy.domain.ao.entity;

import androidx.annotation.Nullable;

import com.czy.domain.vo.entity.home.CommentVo;

public class CommentAo {
    // view
    public CommentVo commentVo;

    // data
    public Long commentId;
    @Nullable
    public Long parentCommentId = null;// 父评论id
    public Long postId;
    public Long commenterId;// userId
}
