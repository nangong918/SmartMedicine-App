package com.czy.feature.nearOnlineLayer.service;

import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.api.domain.ao.post.PostNerResult;

import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/9 15:38
 */
public interface UserActionRecordService {

    // 隐性特征 前端主动http埋点

    /**
     * 上传用户的城市等信息
     * @param ao             用户城市位置信息
     * @param timestamp      特征时间戳[特征时效控制]
     */
    void uploadUserInfo(UserCityLocationInfoAo ao, Long timestamp);

    /**
     * 用户点击帖子（与浏览时长拆开，避免用户直接划掉后台）-> user/item
     * @param userId            用户id
     * @param postId            帖子id
     * @param clickTimestamp    点击时间戳
     * @param timestamp         特征时间戳[特征时效控制]
     */
    void clickPost(Long userId, Long postId, Long clickTimestamp, Long timestamp);

    /**
     * 上传用用的点击帖子 + 浏览时长 -> user/item
     * @param userId            用户id
     * @param postId            帖子id
     * @param browseDuration        浏览时长
     * @param timestamp         特征时间戳[特征时效控制]
     */
    void uploadClickPostAndBrowseTime(Long userId, Long postId, Long browseDuration, Long timestamp);

    // 显性特征 系统内mq埋点

    /**
     * 用户的搜索-> user/item
     * @param userId            用户id
     * @param levelsPostIdMap  搜索结果
     * @param levelsPostEntityScoreMap        搜索句子的ner结果
     * @param timestamp         特征时间戳[特征时效控制]
     */
    void searchPost(Long userId,
                         Map<Integer, List<Long>> levelsPostIdMap,
                         Map<Integer, List<PostNerResult>> levelsPostEntityScoreMap,
                         Map<Integer, List<Integer>> levelsPostLabelScoreMap,
                         Long timestamp);

    /**
     * 操作数据 点赞、收藏、转发-> user/item
     * @param userId            用户id
     * @param postId            帖子id
     * @param operateType       操作类型
     * @param timestamp         特征时间戳[特征时效控制]
     */
    void operatePost(Long userId, Long postId, Integer operateType, Long timestamp);

    /**
     * 操作数据 评论（BERT情感分类NLE：肯定态度，否定态度，中立态度）-> user/item
     * @param userId                用户id
     * @param postId                帖子id
     * @param commentEmotionType    评论态度
     * @param confidenceLevel        置信度
     * @param timestamp             特征时间戳[特征时效控制]
     */
    void commentPost(Long userId, Long postId, Integer commentEmotionType, Double confidenceLevel, Long timestamp);
}
