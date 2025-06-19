package com.czy.api.domain.ao.post;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/18 17:24
 */
@Data
public class PostAo implements Serializable, BaseBean {
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
    private Long releaseTimestamp = System.currentTimeMillis();

    // 阅读数；not null TODO
    private Long readCount = 0L;
    // 点赞数；not null
    private Long likeCount = 0L;
    // 收藏数；not null
    private Long collectCount = 0L;
    // 评论数；not null
    private Long commentCount = 0L;
    // 转发数量
    private Long forwardCount = 0L;

    // post特征
    private List<PostNerResult> nerResults = new ArrayList<>();
}
