package com.czy.feature.service;

import com.czy.api.domain.ao.feature.PostFeatureAo;

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

}
