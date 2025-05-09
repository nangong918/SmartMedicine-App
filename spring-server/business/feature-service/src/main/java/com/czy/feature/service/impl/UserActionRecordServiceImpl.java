package com.czy.feature.service.impl;

import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.feature.service.UserActionRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/9 15:42
 * 1.临时特征：
 *     用户的方向特征和post的热度特征具有时效性的，并且需要快速存储，基本上不会修改。存储在redis
 * 2.历史特征：推荐筛选（推荐过的就不要推荐了）
 *     neo4j的协同过滤和矩阵分解，user相似度计算
 * <p>
 * 根据需求分析：
 *  1.临时特征：快速获取用户最近的上下文
 *  2.历史特征：构建协同过滤，矩阵分解，user相似度计算
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserActionRecordServiceImpl implements UserActionRecordService {

    // 隐性特征 前端主动http埋点

    /**
     * 上传用户的城市等信息
     * @param ao             用户城市位置信息
     * @param timestamp      特征时间戳[特征时效控制]
     */
    @Override
    public void uploadUserInfo(UserCityLocationInfoAo ao, Long timestamp) {

    }

    /**
     * 用户点击帖子（与浏览时长拆开，避免用户直接划掉后台）-> user/item
     * @param userId            用户id
     * @param postId            帖子id
     * @param clickTimestamp    点击时间戳
     * @param timestamp         特征时间戳[特征时效控制]
     */
    @Override
    public void clickPost(Long userId, Long postId, Long clickTimestamp, Long timestamp) {

    }

    /**
     * 上传用用的点击帖子 + 浏览时长 -> user/item
     * @param userId            用户id
     * @param postId            帖子id
     * @param browseTime        浏览时长
     * @param timestamp         特征时间戳[特征时效控制]
     */
    @Override
    public void uploadClickPostAndBrowseTime(Long userId, Long postId, Long browseTime, Long timestamp) {

    }

    // 显性特征 系统内mq埋点

    /**
     * 用户的搜索-> user/item
     * @param userId            用户id
     * @param levelsPostIdList  搜索结果
     * @param nerResults        搜索句子的ner结果
     * @param timestamp         特征时间戳[特征时效控制]
     */
    @Override
    public void searchPost(Long userId, List<List<Long>> levelsPostIdList, List<PostNerResult> nerResults, Long timestamp) {

    }

    /**
     * 操作数据 点赞、收藏、转发-> user/item
     * @param userId            用户id
     * @param postId            帖子id
     * @param operateType       操作类型
     * @param timestamp         特征时间戳[特征时效控制]
     */
    @Override
    public void operatePost(Long userId, Long postId, Integer operateType, Long timestamp) {

    }

    /**
     * 操作数据 评论（BERT情感分类NLE：肯定态度，否定态度，中立态度）-> user/item
     * @param userId                用户id
     * @param postId                帖子id
     * @param commentEmotionType    评论态度
     * @param timestamp             特征时间戳[特征时效控制]
     */
    @Override
    public void commentPost(Long userId, Long postId, Integer commentEmotionType, Long timestamp) {

    }
}
