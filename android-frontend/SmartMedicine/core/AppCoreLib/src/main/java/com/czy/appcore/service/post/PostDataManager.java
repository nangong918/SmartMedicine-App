package com.czy.appcore.service.post;


import com.czy.dal.ao.home.PostAo;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于post信息管理者
 * 管理对象包括:
 *  Home页面的RecommendPosts
 *  Home页面的PopularPosts (保留顶部时间戳)
 *  Community页面的CommunityPosts (保留顶部时间戳)
 * 数据源: http
 */
public class PostDataManager {

    /// 常量数据
    private static final String TAG = PostDataManager.class.getName();

    /// 缓存对象
    // RecommendPosts (只有首页推荐才有大卡片小卡片的区分, 采用ao)
    public final List<PostAo> recommendPosts = new ArrayList<>();
    // PopularPosts
    public final List<PostVo> popularPosts = new ArrayList<>();
    // CommunityPosts
    public final List<PostVo> communityPosts = new ArrayList<>();
}
