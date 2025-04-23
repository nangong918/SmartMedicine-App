package com.czy.api.domain.Do.post.collect;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/4/23 17:13
 */
@Data
public class PostCollectFolderDo {
    @Id
    // collectFolderId；not null
    private Long id;
    // userId；not null
    private Long userId;
    // collectFolderName；not null
    private String collectFolderName;
    // (postId + userId)的联合索引
}
