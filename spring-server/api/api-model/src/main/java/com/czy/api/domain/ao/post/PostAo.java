package com.czy.api.domain.ao.post;

import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 17:24
 */
@Data
public class PostAo {
    private Long id;
    // authorId；not null（索引）
    private Long authorId;
    // fileIds
    private List<Long> fileIds;

    // title；not null
    private String title;
    // content；not null
    private String content;
    // releaseTimestamp；not null
    private Long releaseTimestamp;

    // 点赞数；not null
    private Long likeCount;
    // 收藏数；not null
    private Long collectCount;
    // 评论数；not null
    private Long commentCount;
    // 转发数量
    private Long forwardCount;
}
