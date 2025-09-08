package com.czy.api.domain.bo.post;

/**
 * @author 13225
 * @date 2025/9/8 11:09
 */

import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class CommentBo {
    // vo
    @Nullable
    public Long avatarFileId;
    public String userName;
    public String replyUserName;
    // comment
    public Long commentTimestamp;
    public String commentContent;

    // data
    public Long commentId;
    @Nullable
    public Long parentCommentId = null;// 父评论id
    public Long postId;
    public Long commenterId;// userId
}
