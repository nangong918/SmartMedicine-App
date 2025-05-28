package com.czy.api.domain.ao.post;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/18 17:24
 */
@Data
public class PostInfoUrlAo {
    public Long id;
    // authorId；not null（索引）
    public Long authorId;
    // 作者名称
    public String authorName;
    // 作者头像
    public String authorAvatarUrl;
    // fileUrl 只展示1个
    public String fileUrl;
    // title；not null
    public String title;
    // releaseTimestamp；not null
    public Long releaseTimestamp;

    // 阅读数；not null
    public Long readCount = 0L;
    // 点赞数；not null
    public Long likeCount = 0L;
    // 收藏数；not null
    public Long collectCount = 0L;
    // 评论数；not null
    public Long commentCount = 0L;
    // 转发数量
    public Long forwardCount = 0L;
}
