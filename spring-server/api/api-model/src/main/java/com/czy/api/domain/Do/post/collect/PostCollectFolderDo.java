package com.czy.api.domain.Do.post.collect;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/4/23 17:13
 * 一对多关系；不是多对多，所以id就是collectFolderId
 */
@Data
public class PostCollectFolderDo implements Serializable {
    @Id
    // collectFolderId；not null
    private Long id;
    // userId；not null
    private Long userId;
    // collectFolderName；not null
    private String collectFolderName;
    // (postId + userId)的联合索引
}
