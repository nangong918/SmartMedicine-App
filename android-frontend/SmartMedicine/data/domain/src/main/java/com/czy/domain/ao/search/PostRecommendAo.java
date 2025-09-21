package com.czy.domain.ao.search;


import com.czy.domain.vo.entity.home.PostPreviewVo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/9 14:13
 */
public class PostRecommendAo {
    /**
     * 推荐类型
     * @see com.czy.domain.constant.search.PostRecommendResult
     */
    public Integer recommendType;
    public List<PostPreviewVo> postPreviewVos;
}
