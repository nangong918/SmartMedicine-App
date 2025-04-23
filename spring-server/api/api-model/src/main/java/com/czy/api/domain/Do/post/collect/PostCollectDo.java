package com.czy.api.domain.Do.post.collect;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/4/23 17:13
 */
@Data
public class PostCollectDo {
    @Id
    // collectFolderId；not null
    private Long id;
    // postId；not null
    private Long postId;
    // collectFolderId；not null （索引）
    private Long collectFolderId;
}
