package com.czy.api.domain.vo.post;


import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVo implements Serializable {
    // user
    public String avatarUrl;
    public String userName;
    public String replyUserName;
    // comment
    public String commentTime;
    public String commentContent;
}
