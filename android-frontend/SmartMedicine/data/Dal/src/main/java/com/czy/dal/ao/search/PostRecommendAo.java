package com.czy.dal.ao.search;


import com.czy.dal.ao.home.PostInfoUrlAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/9 14:13
 */
public class PostRecommendAo {
    /**
     * 推荐类型
     * @see com.czy.dal.constant.search.PostRecommendResult
     */
    public Integer recommendType;
    public List<PostInfoUrlAo> postInfoUrlAos;
}
