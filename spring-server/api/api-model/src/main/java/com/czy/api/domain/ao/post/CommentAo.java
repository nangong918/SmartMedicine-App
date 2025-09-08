package com.czy.api.domain.ao.post;


import com.czy.api.domain.vo.post.CommentVo;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@Data
public class CommentAo implements Serializable {
    // view
    public CommentVo commentVo;

    // data
    public Long commentId;
    @Nullable
    public Long parentCommentId = null;// 父评论id
    public Long postId;
    public Long commenterId;// userId
}
