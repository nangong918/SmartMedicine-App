package com.czy.feature.service;

import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.feature.PostHeatAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/8 18:21
 * 1. 发布的时候用户手动打分区标签 + Bert模型对文章进行分类：#日常分享 #专业医疗知识 #养生技巧 #医疗新闻 #其他
 * service计算滕峥
 */
public interface PostFeatureService {

    /**
     * 获取post的特征
     * @param postId    postId
     * @return          PostFeatureAo
     */
    PostFeatureAo getPostFeature(Long postId);

    /**
     * 获取热门帖子
     * 提供给定时任务每日调用的方法，获取之后存储在redis
     */
    void getHotPosts();

    /**
     * 提供给用户调用的方法
     * @param limitNum  获取多少条
     * @return           List<PostHeatAo>
     */
    List<PostHeatAo> getHotPosts(Integer limitNum);

    // 获取某条帖子的热度
    PostHeatAo getPostHeat(Long postId);

    // 获取list<postId>的热度
    List<PostHeatAo> getPostHeats(List<Long> postIds);

}
