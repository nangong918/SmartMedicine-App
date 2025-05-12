package com.czy.feature.service.impl;

import com.czy.api.api.post.PostSearchService;
import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.feature.PostTypeEnum;
import com.czy.api.constant.feature.UserActionRedisKey;
import com.czy.api.domain.Do.neo4j.rels.UserPostRelation;
import com.czy.api.domain.Do.post.post.PostDetailDo;
import com.czy.api.domain.ao.feature.NerFeatureScoreAo;
import com.czy.api.domain.ao.feature.PostBrowseDurationAo;
import com.czy.api.domain.ao.feature.PostClickTimeAo;
import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.feature.PostSearchTimeAo;
import com.czy.api.domain.ao.feature.ScoreAo;
import com.czy.api.domain.ao.feature.UserCityLocationInfoAo;
import com.czy.api.domain.ao.feature.UserEntityFeatureAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.feature.rule.RulePostReadTime;
import com.czy.feature.service.FeatureStorageService;
import com.czy.feature.service.PostFeatureService;
import com.czy.feature.service.UserActionRecordService;
import com.czy.springUtils.debug.DebugConfig;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/5/9 15:42
 * 1.临时特征：
 *     用户的方向特征和post的热度特征具有时效性的，并且需要快速存储，基本上不会修改。存储在redis
 * 2.历史特征：推荐筛选（推荐过的就不要推荐了）
 *     neo4j的协同过滤和矩阵分解，user相似度计算
 * <p>
 * 根据需求分析：
 *  1.临时特征：快速获取用户最近的上下文                 临时特征会热衰减  （数据 + 时间：热衰减）
 *      存储数据结构：
 *          1.user_post: userId + postId + clickTime -> PostClickTimeAo
 *          2.user_entity: userId + List<NerPostResult> + clickTime
 *  2.历史特征：构建协同过滤，矩阵分解，user相似度计算     持久特征不会热衰减 （固定数据，不执行定时任务，不热衰减，叠加改变）
 *      存储数据结构：
 *          1.user_post: userId + PostFeatureAo + scoreAo -> UserPostFeatureAo
 *          2.user_entity: userId + PostFeatureAo + scoreAo -> UserEntityFeatureAo
 * <p>
 * 关于特征计算：大数据：离线，近线，在线（等整个前后端完成再加入大数据）
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserActionRecordServiceImpl implements UserActionRecordService {

    private final RedissonService redissonService;
    private final UserFeatureRepository userFeatureRepository;
    private final DebugConfig debugConfig;
    private final PostFeatureService postFeatureService;
    @Reference(protocol = "dubbo", version = "1.0.0", check = false)
    private PostSearchService postSearchService;
    private final RulePostReadTime rulePostReadTime;
    private final FeatureStorageService featureStorageService;
    /// 隐性特征 前端主动http埋点

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


    // 存储临时特征到Redis
    private void addFeatureToRedis(String featureKey, Object ao, Long timestamp) {
        redissonService.zAdd(
                featureKey,
                ao,
                timestamp.doubleValue(),
                FeatureConstant.FEATURE_EXPIRE_TIME_SECOND
        );
    }

    /**
     * 用户点击帖子（与浏览时长拆开，避免用户直接划掉后台）-> user/item
     * click只增加clickTime
     * 临时特征：user-post（查询特征的时候自行计算;计算之后存储在Redis中）
     * 历史特征：user-post/entity/label
     * user:item
     * @param userId            用户id
     * @param postId            帖子id
     * @param clickTimestamp    点击时间戳
     * @param timestamp         特征时间戳[特征时效控制]
     */
    @Override
    public void clickPost(Long userId, Long postId, Long clickTimestamp, Long timestamp) {
        String userFeatureKey = UserActionRedisKey.USER_FEATURE_CLICK_POST_REDIS_KEY + userId + ":" + postId;

        // 1. user-post 特征
        PostClickTimeAo postClickTimeAo = new PostClickTimeAo();
        postClickTimeAo.setUserId(userId);
        postClickTimeAo.setPostId(postId);
        postClickTimeAo.setClickTime(clickTimestamp);

        // 1.1 user-post-临时特征：记录30天
        // 用户 + post维度：记录用户点击的帖子及时间（ZSet）
        addFeatureToRedis(
                userFeatureKey,
                postClickTimeAo,
                timestamp
        );

        // 1.2 user-post-历史特征：记录入neo4j;内置检查是否已经创建的方法
        /*
        "MATCH (u:user {id: $userId}) " +
            "MATCH (p:post {id: $postId}) " +
            "MERGE (u)-[r:user_post]->(p) " +
            "ON CREATE SET r.clickTimes = 1, " +
            "r.implicitScore = 0.0, " +
            "r.explicitScore = 0.0, " +
            "r.lastUpdateTimestamp = datetime() " +
            "ON MATCH SET r.clickTimes = r.clickTimes + 1, " +
            "ON MATCH SET r.clickTimes = r.clickTimes + 1, r.lastUpdateTime = datetime() " +
            "RETURN r"
         */
        UserPostRelation userPostRelation = userFeatureRepository.createUserPostRelation(userId, postId);
        if (debugConfig.isDebug()){
            if (userPostRelation != null){
                log.info("改变user-post点击次数：{}", userPostRelation.getClickTimes());
            }
            else {
                log.warn("创建user-post关系失败");
            }
        }

        // 2. user-entity 特征
        // 获取post特征
        PostFeatureAo postFeatureAo = postFeatureService.getPostFeature(postId);

        // 2.1 user-entity临时特征取消存储，改用现场计算

        // 2.2 user-entity-历史特征：记录入neo4j
        // 点击操作只增加历史的权重，不增加历史的分数，分数由浏览时长控制
        // 将 post的信息关联user存入neo4j
        featureStorageService.uploadUserEntityFeature(postFeatureAo, userId);

    }


    public UserEntityFeatureAo getUserEntityFeature(@NotNull PostFeatureAo postFeatureAo,
                                                    Integer clickTimes, Double implicitScore, Double explicitScore) {
        if (implicitScore == null){
            implicitScore = 0.0;
        }
        if (explicitScore == null){
            explicitScore = 0.0;
        }

        UserEntityFeatureAo userEntityFeatureAo = new UserEntityFeatureAo();
        // 1.user-entity
        if (!CollectionUtils.isEmpty(postFeatureAo.getPostNerResultList())){
            for (PostNerResult postNerResult : postFeatureAo.getPostNerResultList()) {
                String keyWord = postNerResult.getKeyWord();
                // nerFeatureScoreAo
                NerFeatureScoreAo nerFeatureScoreAo = new NerFeatureScoreAo();
                nerFeatureScoreAo.setNerType(postNerResult.getNerType());
                nerFeatureScoreAo.setKeyWord(keyWord);

                // 计算scoreAo
                ScoreAo scoreAo = getScoreAo(clickTimes, implicitScore, explicitScore);

                // 添加score
                nerFeatureScoreAo.setScore(scoreAo);
                userEntityFeatureAo.getNerFeatureScoreMap().put(keyWord, nerFeatureScoreAo);
            }
        }

        // 2.user-label
        // 非空且非其他（其他由系统自行控制）
        if (postFeatureAo.getPostType() != null && !postFeatureAo.getPostType().equals(PostTypeEnum.OTHER.getCode())){
            ScoreAo scoreAo = getScoreAo(clickTimes, implicitScore, explicitScore);

            // 添加score
            userEntityFeatureAo.getLabelScoreMap().put(postFeatureAo.getPostType(), scoreAo);
        }
        return userEntityFeatureAo;
    }

    // UserEntityFeatureAo + PostFeatureAo -> UserEntityFeatureAo
    // PostFeatureAo -> UserEntityFeatureAo
    @Deprecated
    public UserEntityFeatureAo getUserEntityFeature(@NotNull PostFeatureAo postFeatureAo, UserEntityFeatureAo oldUserEntityFeatureAo,
                                                    Integer clickTimes, Double implicitScore, Double explicitScore) {
        if (implicitScore == null){
            implicitScore = 0.0;
        }
        if (explicitScore == null){
            explicitScore = 0.0;
        }

        // 如果原先从实体存在就取来累加分值
        UserEntityFeatureAo cloneUserUserEntityFeatureAo = null;
        if (oldUserEntityFeatureAo != null){
            try {
                cloneUserUserEntityFeatureAo = (UserEntityFeatureAo) oldUserEntityFeatureAo.clone();
            } catch (CloneNotSupportedException e) {
                log.error("clone error", e);
            }
        }

        UserEntityFeatureAo userEntityFeatureAo = new UserEntityFeatureAo();

        // 1.user-entity
        if (!CollectionUtils.isEmpty(postFeatureAo.getPostNerResultList())){
            for (PostNerResult postNerResult : postFeatureAo.getPostNerResultList()) {
                String keyWord = postNerResult.getKeyWord();
                // nerFeatureScoreAo
                NerFeatureScoreAo nerFeatureScoreAo = new NerFeatureScoreAo();
                nerFeatureScoreAo.setNerType(postNerResult.getNerType());
                nerFeatureScoreAo.setKeyWord(keyWord);

                // 计算scoreAo
                ScoreAo scoreAo = getScoreAo(clickTimes, implicitScore, explicitScore);

                ScoreAo oldScore = getEntityOldScoreAo(cloneUserUserEntityFeatureAo, keyWord);
                // 叠加分数
                scoreAo.add(oldScore);

                // 添加score
                nerFeatureScoreAo.setScore(scoreAo);
                userEntityFeatureAo.getNerFeatureScoreMap().put(keyWord, nerFeatureScoreAo);
            }
        }

        // 2.user-label
        // 非空且非其他（其他由系统自行控制）
        if (postFeatureAo.getPostType() != null && !postFeatureAo.getPostType().equals(PostTypeEnum.OTHER.getCode())){
            ScoreAo scoreAo = getScoreAo(clickTimes, implicitScore, explicitScore);
            ScoreAo oldScoreAo = getLabelOldScoreAo(cloneUserUserEntityFeatureAo, postFeatureAo.getPostType());

            // 叠加分数
            scoreAo.add(oldScoreAo);

            // 添加score
            userEntityFeatureAo.getLabelScoreMap().put(postFeatureAo.getPostType(), scoreAo);
        }
        return userEntityFeatureAo;
    }

    private ScoreAo getEntityOldScoreAo(UserEntityFeatureAo userEntityFeatureAo, String keyWord){
        return Optional.ofNullable(userEntityFeatureAo)
                    .map(UserEntityFeatureAo::getNerFeatureScoreMap)
                    .map(map -> map.get(keyWord))
                    .map(NerFeatureScoreAo::getScore)
                    .orElse(new ScoreAo());
    }

    private ScoreAo getLabelOldScoreAo(UserEntityFeatureAo userEntityFeatureAo, Integer label){
        return Optional.ofNullable(userEntityFeatureAo)
                .map(UserEntityFeatureAo::getLabelScoreMap)
                .map(map -> map.get(label))
                .orElse(new ScoreAo());
    }

    private ScoreAo getScoreAo(int clickTimes, double implicitScore, double explicitScore){
        ScoreAo scoreAo = new ScoreAo();
        scoreAo.setClickTimes(clickTimes);
        scoreAo.setImplicitScore(implicitScore);
        scoreAo.setExplicitScore(explicitScore);
        return scoreAo;
    }

//    private UserEntityFeatureAo searchUserEntityFeatureAo(Long userId) {
//
//        List<DiseaseDo> diseaseDoList = userFeatureRepository.findDiseasesByPostId(userId);
//        List<ChecksDo> checksDoList = userFeatureRepository.findChecksByPostId(userId);
//        List<DepartmentsDo> departmentsDoList = userFeatureRepository.findDepartmentsByPostId(userId);
//        List<DrugsDo> drugsDoList = userFeatureRepository.findDrugsByPostId(userId);
//        List<FoodsDo> foodsDoList = userFeatureRepository.findFoodsByPostId(userId);
//        List<ProducersDo> producersDoList = userFeatureRepository.findProducersByPostId(userId);
//        List<RecipesDo> recipesDoList = userFeatureRepository.findRecipesByPostId(userId);
//        List<SymptomsDo> symptomsDoList = userFeatureRepository.findSymptomsByPostId(userId);
//        List<PostLabelNeo4jDo> postLabelNeo4jDoList = userFeatureRepository.findPostLabelsByPostId(userId);
//    }

    @Deprecated
    private ScoreAo searchUserEntityScoreAo(Long userId, String entityLabel, String entityName, String entityRelation) {
        Optional<Map<String, Object>> optionalScoreMap = userFeatureRepository.findUserEntityPostRelationScoreAo(userId, entityLabel, entityName, entityRelation);
        ScoreAo scoreAo = new ScoreAo();
        optionalScoreMap.ifPresent(map -> {
            Integer clickTimes = Optional.ofNullable(map.get("clickTimes"))
                    .map(o -> (Integer) o)
                    .orElse(0);
            Double implicitScore = Optional.ofNullable(map.get("implicitScore"))
                    .map(o -> (Double) o)
                    .orElse(0.0);
            Double explicitScore = Optional.ofNullable(map.get("explicitScore"))
                    .map(o -> (Double) o)
                    .orElse(0.0);

            scoreAo.setClickTimes(clickTimes);
            scoreAo.setImplicitScore(implicitScore);
            scoreAo.setExplicitScore(explicitScore);
        });
        return scoreAo;
    }


    /**
     * 上传用用的点击帖子 + 浏览时长 -> user/item
     * @param userId            用户id
     * @param postId            帖子id
     * @param browseDuration        浏览时长
     * @param timestamp         特征时间戳[特征时效控制]
     */
    @Override
    public void uploadClickPostAndBrowseTime(Long userId, Long postId, Long browseDuration, Long timestamp) {
        String userFeatureKey = UserActionRedisKey.USER_FEATURE_BROWSE_POST_REDIS_KEY + userId + ":" + postId;

        PostDetailDo postDetailDo = postSearchService.searchPostDetailById(postId);
        if (postDetailDo == null){
            log.warn("上传用用的点击帖子 + 浏览时长特征失败，postDetailDo is null, postId:{}", postId);
            return;
        }

        // 计算浏览时长的分数 （隐性分数）
        Double implicitScore = getBrowseTimeScore(browseDuration, postDetailDo);

        /// 1.临时特征：记录30天
        PostBrowseDurationAo postBrowseDurationAo = new PostBrowseDurationAo();
        postBrowseDurationAo.setUserId(userId);
        postBrowseDurationAo.setPostId(postId);
        postBrowseDurationAo.setBrowseDuration(browseDuration);
        postBrowseDurationAo.setImplicitScore(implicitScore);
        // ZSet形式存储在redis中
        addFeatureToRedis(
                userFeatureKey,
                postBrowseDurationAo,
                timestamp
        );

        /// 2.历史特征：记录入neo4j;内置检查是否已经创建的方法
        /// 2.1 user-post特征
        UserPostRelation userPostRelation = userFeatureRepository.createUserPostRelation(userId, postId);
        ScoreAo user_postScoreAo = getScoreAo(userPostRelation, implicitScore);
        // 更新关系
        userFeatureRepository.updateUserPostRelation(
                userId,
                postId,
                user_postScoreAo.getClickTimes(),
                user_postScoreAo.getImplicitScore(),
                user_postScoreAo.getExplicitScore()
        );

        // 获取postFeatureAo特征
        PostFeatureAo postFeatureAo = postFeatureService.getPostFeature(postId);
        if (postFeatureAo == null){
            log.warn("post不存在特征，postId:{}", postId);
            return;
        }

        /// 2.2 user-entity/label 特征
        // 获取UserEntityFeatureAo
        UserEntityFeatureAo userEntityFeatureAo = getUserEntityFeature(postFeatureAo, 0,
                implicitScore, 0.0);

        // 存储user-entity/label的历史特征
        featureStorageService.saveUserEntityFeature(userId, userEntityFeatureAo);
    }

    @NotNull
    private static ScoreAo getScoreAo(UserPostRelation userPostRelation, Double implicitScore) {
        ScoreAo user_postScoreAo = new ScoreAo();
        if (userPostRelation != null){
            user_postScoreAo.setClickTimes(userPostRelation.getClickTimes() + 1);
            user_postScoreAo.setImplicitScore(userPostRelation.getImplicitScore() + implicitScore);
            user_postScoreAo.setExplicitScore(userPostRelation.getExplicitScore());
        }
        else {
            user_postScoreAo.setClickTimes(1);
            user_postScoreAo.setImplicitScore(implicitScore);
            user_postScoreAo.setExplicitScore(0.0);
        }
        return user_postScoreAo;
    }

    private Double getBrowseTimeScore(Long browseDuration, PostDetailDo postDetailDo) {
        Integer postWordCount = postDetailDo.getContent().length();
        return rulePostReadTime.execute(postWordCount, browseDuration);
    }

    /// 显性特征 系统内mq埋点

    /**
     * 用户的搜索-> user/item
     * @param userId            用户id
     * @param levelsPostIdList  搜索结果
     * @param levelsNerResults        搜索句子的ner结果
     * @param timestamp         特征时间戳[特征时效控制]
     */
    @Override
    public void searchPost(Long userId, List<List<Long>> levelsPostIdList, List<List<PostNerResult>> levelsNerResults, Long timestamp) {
        // 1.临时特征
        PostSearchTimeAo postSearchTimeAo = new PostSearchTimeAo();

        // 2.历史特征
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
