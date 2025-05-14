package com.czy.api.domain.ao.search;

import com.czy.api.domain.ao.post.PostInfoUrlAo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/9 14:13
 */
@Data
public class PostRecommendAo {
    /**
        @see com.czy.api.constant.search.result.PostRecommendResult
     */
    private Integer recommendType;
    private List<PostInfoUrlAo> postInfoUrlAos;
}
