package com.czy.feature.service.impl;

import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.feature.UserActionRedisKey;
import com.czy.api.domain.Do.neo4j.rels.UserPostRelation;
import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.feature.service.UserActionRecordService;
import com.czy.springUtils.debug.DebugConfig;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    private final RedissonService redissonService;
    private final UserFeatureRepository userFeatureRepository;
    private final DebugConfig debugConfig;

    // 隐性特征 前端主动http埋点

    /**
     * 上传用户的城市等信息
     * @param ao             用户城市位置信息
     * @param timestamp      特征时间戳[特征时效控制]
     */
    @Override
    public void uploadUserInfo(UserCityLocationInfoAo ao, Long timestamp) {
        // 此不需要历史特征，只作为当前的特征
        HashMap<String, Object> data = new HashMap<>();
        data.put("cityName", ao.getCityName());
        data.put("longitude", ao.getLongitude());
        data.put("latitude", ao.getLatitude());

        String key = UserActionRedisKey.USER_FEATURE_CITY_LOCATION_REDIS_KEY + ao.getUserId();

        // 存储 HashMap
        redissonService.saveObjectHaseMap(key, data, FeatureConstant.FEATURE_EXPIRE_TIME_SECOND);
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
        String userFeatureKey = UserActionRedisKey.USER_FEATURE_CLICK_POST_REDIS_KEY + userId;
        String postHeatKey = UserActionRedisKey.POST_HEAT_CLICK_REDIS_KEY + postId;

        // 临时特征：记录30天
        // 用户维度：记录用户点击的帖子及时间（ZSet）
        redissonService.zAdd(
                userFeatureKey,
                postId.toString(),
                clickTimestamp.doubleValue(),
                FeatureConstant.FEATURE_EXPIRE_TIME_SECOND);
        // 帖子维度：记录被点击的热度（ZSet）
        redissonService.zAdd(
                postHeatKey,
                userId.toString(),
                clickTimestamp.doubleValue(),
                FeatureConstant.FEATURE_EXPIRE_TIME_SECOND
        );

        // 历史特征：记录入neo4j;内置检查是否已经创建的方法
        /*
        "MATCH (u:user {id: $userId}) " +
            "MATCH (p:post {id: $postId}) " +
            "MERGE (u)-[r:user_post]->(p) " +
            "ON CREATE SET r.weight = 1, r.lastUpdateTime = datetime() " +
            "ON MATCH SET r.weight = r.weight + 1, r.lastUpdateTime = datetime() " +
            "RETURN r"
         */
        UserPostRelation userPostRelation = userFeatureRepository.createUserPostRelation(userId, postId);
        if (debugConfig.isDebug()){
            if (userPostRelation != null){
                log.info("改变user-post关系权重：{}", userPostRelation.getWeight());
            }
            else {
                log.warn("创建user-post关系失败");
            }
        }
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
