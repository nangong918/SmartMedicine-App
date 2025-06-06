package com.czy.api.domain.Do.post.post;

import cn.hutool.core.util.IdUtil;
import json.BaseBean;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/4/22 9:42
 */
@Data
public class PostFilesDo implements BaseBean {
    // not null
    @Id
    private Long id = IdUtil.getSnowflakeNextId();
    // not null + 索引
    private Long postId;
    // not null
    private Long fileId;
}
