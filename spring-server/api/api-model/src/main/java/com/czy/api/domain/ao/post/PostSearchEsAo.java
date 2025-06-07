package com.czy.api.domain.ao.post;

import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/5/6 15:06
 */
@Data
public class PostSearchEsAo implements Serializable {
    // 匹配到的数量
    private int matchedCount = 0;
    private PostDetailEsDo postDetailEsDo;
}
