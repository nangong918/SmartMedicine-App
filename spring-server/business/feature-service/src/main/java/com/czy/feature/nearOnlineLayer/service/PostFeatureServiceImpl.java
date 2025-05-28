package com.czy.feature.nearOnlineLayer.service;

import com.czy.api.constant.feature.FeatureConstant;
import com.czy.api.constant.feature.PostTypeEnum;
import com.czy.api.constant.feature.UserActionRedisKey;
import com.czy.api.constant.post.DiseasesKnowledgeGraphEnum;
import com.czy.api.domain.Do.neo4j.ChecksDo;
import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.DrugsDo;
import com.czy.api.domain.Do.neo4j.FoodsDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.ProducersDo;
import com.czy.api.domain.Do.neo4j.RecipesDo;
import com.czy.api.domain.Do.neo4j.SymptomsDo;
import com.czy.api.domain.ao.feature.PostBrowseDurationAo;
import com.czy.api.domain.ao.feature.PostClickTimeAo;
import com.czy.api.domain.ao.feature.PostExplicitPostScoreAo;
import com.czy.api.domain.ao.feature.PostExplicitTimeAo;
import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.feature.PostHeatAo;
import com.czy.api.domain.ao.feature.ScoreDaysAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.mapper.PostRepository;
import com.czy.feature.rule.RulePostHeat;
import com.czy.api.api.feature.PostFeatureService;
import com.utils.mvc.redisson.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/5/10 14:03
 */
@Slf4j
@RequiredArgsConstructor
@Service
@org.apache.dubbo.config.annotation.Service(protocol = "dubbo", version = "1.0.0")
public class PostFeatureServiceImpl implements PostFeatureService {
    private final PostRepository postRepository;
    private final RedissonService redissonService;

    private final RulePostHeat rulePostHeat;

    // TODO 优化：特征查找之后存储在Redis，缓存击穿再执行数据库查询。
    //  并且更改策略，要预先加载当前热门的Top-k
    @Override
    public PostFeatureAo getPostFeature(Long postId) {
        List<DiseaseDo> diseases = postRepository.findDiseasesByPostId(postId);
        List<ChecksDo> checks = postRepository.findChecksByPostId(postId);
        List<DepartmentsDo> departments = postRepository.findDepartmentsByPostId(postId);
        List<DrugsDo> drugs = postRepository.findDrugsByPostId(postId);
        List<FoodsDo> foods = postRepository.findFoodsByPostId(postId);
        List<ProducersDo> producers = postRepository.findProducersByPostId(postId);
        List<RecipesDo> recipes = postRepository.findRecipesByPostId(postId);
        List<SymptomsDo> symptoms = postRepository.findSymptomsByPostId(postId);
        PostFeatureAo postFeatureAo = new PostFeatureAo();
        List<PostNerResult> postNerResultList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(diseases)){
            for (DiseaseDo disease : diseases) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(disease.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.DISEASES.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(checks)){
            for (ChecksDo check : checks) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(check.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.CHECKS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(departments)){
            for (DepartmentsDo department : departments) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(department.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.DEPARTMENTS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(drugs)){
            for (DrugsDo drug : drugs) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(drug.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.DRUGS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(foods)){
            for (FoodsDo food : foods) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(food.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.FOODS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(producers)){
            for (ProducersDo producer : producers) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(producer.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.PRODUCERS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(recipes)){
            for (RecipesDo recipe : recipes) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(recipe.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.RECIPES.getName());
                postNerResultList.add(postNerResult);
            }
        }
        if (!CollectionUtils.isEmpty(symptoms)){
            for (SymptomsDo symptom : symptoms) {
                PostNerResult postNerResult = new PostNerResult();
                postNerResult.setKeyWord(symptom.getName());
                postNerResult.setNerType(DiseasesKnowledgeGraphEnum.SYMPTOMS.getName());
                postNerResultList.add(postNerResult);
            }
        }
        postFeatureAo.setPostNerResultList(postNerResultList);
        Optional<PostNeo4jDo> postNeo4jDo = postRepository.findById(postId);
        postNeo4jDo.ifPresent(post -> {
            // 处理 post 对象
            if (StringUtils.hasText(post.getLabel())){
                PostTypeEnum postTypeEnum = PostTypeEnum.getByName(post.getLabel());
                postFeatureAo.setPostType(postTypeEnum.getCode());
            }
        });
        return postFeatureAo;
    }

    @Override
    public Map<Long, PostFeatureAo> getPostFeatures(List<Long> postIds) {
        Map<Long, PostFeatureAo> postFeatureAos = new HashMap<>();
        for (Long postId : postIds) {
            postFeatureAos.put(postId, getPostFeature(postId));
        }
        return postFeatureAos;
    }


    @Override
    public List<PostHeatAo> getHotPosts() {
        // 1.计算时间戳
        long currentTime = System.currentTimeMillis();
        // 30天前的时间戳
        long thirtyDaysAgoTime = currentTime - FeatureConstant.FEATURE_EXPIRE_TIME_SECOND * 1000L;

        // 2.获取Redis临时特征
        String userClickFeatureKey = UserActionRedisKey.USER_FEATURE_CLICK_POST_REDIS_KEY + "*" + ":*";
        String userBrowseFeatureKey = UserActionRedisKey.USER_FEATURE_BROWSE_POST_REDIS_KEY + "*" + ":*";
        String userSearchFeatureKey = UserActionRedisKey.USER_FEATURE_SEARCH_POST_REDIS_KEY + "*";
        String userOperateFeatureKey = UserActionRedisKey.USER_FEATURE_OPERATION_POST_REDIS_KEY + "*";
        String userCommentFeatureKey = UserActionRedisKey.USER_FEATURE_COMMENT_POST_REDIS_KEY + "*";
        // 获取pattern
        Collection<String> userClickFeatureKeys = redissonService.getKeysByPattern(userClickFeatureKey);
        Collection<String> userBrowseFeatureKeys = redissonService.getKeysByPattern(userBrowseFeatureKey);
        Collection<String> userSearchFeatureKeys = redissonService.getKeysByPattern(userSearchFeatureKey);
        Collection<String> userOperateFeatureKeys = redissonService.getKeysByPattern(userOperateFeatureKey);
        Collection<String> userCommentFeatureKeys = redissonService.getKeysByPattern(userCommentFeatureKey);

        // 此处获取的是user全部特征的记录
        List<Collection<Object>> userFeatureList = new ArrayList<>();
        // 此处获取的是user:*的全部记录
        Collection<Object> userClickFeatures = new ArrayList<>();
        Collection<Object> userBrowseFeatures = new ArrayList<>();
        Collection<Object> userSearchFeatures = new ArrayList<>();
        Collection<Object> userOperateFeatures = new ArrayList<>();
        Collection<Object> userCommentFeatures = new ArrayList<>();
        for (String key : userClickFeatureKeys) {
            // 此处获取的是userId:postId的多个记录
            Collection<Object> userClickFeature = redissonService.zRangeByScore(
                    key,
                    (double) thirtyDaysAgoTime,
                    (double) currentTime);
            userClickFeatures.addAll(userClickFeature);
        }
        for (String key : userBrowseFeatureKeys) {
            Collection<Object> userBrowseFeature = redissonService.zRangeByScore(
                    key,
                    (double) thirtyDaysAgoTime,
                    (double) currentTime);
            userBrowseFeatures.addAll(userBrowseFeature);
        }
        for (String key : userSearchFeatureKeys) {
            Collection<Object> userSearchFeature = redissonService.zRangeByScore(
                    key,
                    (double) thirtyDaysAgoTime,
                    (double) currentTime);
            userSearchFeatures.addAll(userSearchFeature);
        }
        for (String key : userOperateFeatureKeys){
            Collection<Object> userOperateFeature = redissonService.zRangeByScore(
                    key,
                    (double) thirtyDaysAgoTime,
                    (double) currentTime);
            userOperateFeatures.addAll(userOperateFeature);
        }
        for (String key : userCommentFeatureKeys){
            Collection<Object> userCommentFeature = redissonService.zRangeByScore(
                    key,
                    (double) thirtyDaysAgoTime,
                    (double) currentTime);
            userCommentFeatures.addAll(userCommentFeature);
        }

        // 全部加入 userFeatureList
        userFeatureList.add(userClickFeatures);
        userFeatureList.add(userBrowseFeatures);
        userFeatureList.add(userSearchFeatures);
        userFeatureList.add(userOperateFeatures);
        userFeatureList.add(userCommentFeatures);

        // 3.计算热度 + 排序
        return getTempPostHeats(userFeatureList);
    }

    @Override
    public List<PostHeatAo> getHotPosts(Integer limitNum) {
        // 1.处理入参：最大数
        if (limitNum > FeatureConstant.HOT_POST_MAX_NUM){
            limitNum = FeatureConstant.HOT_POST_MAX_NUM;
        }
        else if (limitNum < 0){
            limitNum = 1;
        }

        // 2.获取 Redis 中的 ZSet
        String redisKey = UserActionRedisKey.POST_HEAT_LIST_REDIS_KEY_PREFIX;

        // 3.获取热度最高的帖子
        Collection<Object> hotPostValues = redissonService.zReverseRange(redisKey, 0, limitNum - 1);

        // 4.转换成 List<PostHeatAo>
        List<PostHeatAo> hotPosts = new ArrayList<>();
        for (Object value : hotPostValues) {
            // 假设需要将 Object 转换为 PostHeatAo
            if (value instanceof PostHeatAo) {
                hotPosts.add((PostHeatAo) value);
            }
        }

        return hotPosts;
    }

    // 定义特征处理器接口
    interface FeatureProcessor<T> {
        void process(T feature,
                     Map<Long, List<ScoreDaysAo>> postFeaturesMap);
    }

    // 通用特征处理方法
    private <T> void processFeatureCollection(Collection<Object> features, Class<T> clazz,
                                              FeatureProcessor<T> processor,
                                              Map<Long, List<ScoreDaysAo>> postFeaturesMap) {
        if (CollectionUtils.isEmpty(features)){
            return;
        }

        features.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .forEach(f -> processor.process(f, postFeaturesMap));
    }

    private List<PostHeatAo> getTempPostHeats(
            List<Collection<Object>> userFeatureList
    ){
        Map<Long, List<ScoreDaysAo>> postFeaturesMap = new HashMap<>();

        // 1.点击特征处理器
        FeatureProcessor<PostClickTimeAo> clickProcessor = (
                feature, postMap) -> {
            Long postId = feature.getPostId();
            Long clickTime = feature.getClickTime();
            if (clickTime == null){
                return;
            }

            List<ScoreDaysAo> postFeatures = postMap.computeIfAbsent(postId, k -> new ArrayList<>());
            ScoreDaysAo scoreDaysAo = new ScoreDaysAo();
            scoreDaysAo.setDays(getDays(clickTime));
            int clickTimes = Math.toIntExact(feature.getClickTime());
            scoreDaysAo.getScoreAo().setClickTimes(clickTimes);
            postFeatures.add(scoreDaysAo);
        };

        // 2.浏览特征处理器
        FeatureProcessor<PostBrowseDurationAo> browseProcessor = (
                feature, postMap) -> {
            Long postId = feature.getPostId();
            Long timestamp = feature.getTimestamp();
            if (timestamp == null) {
                return;
            }

            // 处理帖子特征
            List<ScoreDaysAo> postFeatures = postMap.computeIfAbsent(postId, k -> new ArrayList<>());
            ScoreDaysAo postScoreDaysAo = new ScoreDaysAo();
            postScoreDaysAo.setDays(getDays(timestamp));
            postScoreDaysAo.getScoreAo().setImplicitScore(feature.getImplicitScore());
            postFeatures.add(postScoreDaysAo);
        };

        // 3.搜索特征处理器
        FeatureProcessor<PostExplicitTimeAo> searchProcessor =
                this::processPostFeatures;

        // 4.操作特征处理器
        FeatureProcessor<PostExplicitTimeAo> operateProcessor =
                this::processPostFeatures;

        // 5.评论帖子处理器
        FeatureProcessor<PostExplicitTimeAo> commentProcessor =
                this::processPostFeatures;

        // 6.处理各类型特征
        processFeatureCollection(userFeatureList.get(0), PostClickTimeAo.class, clickProcessor,
                postFeaturesMap);
        processFeatureCollection(userFeatureList.get(1), PostBrowseDurationAo.class, browseProcessor,
                postFeaturesMap);
        processFeatureCollection(userFeatureList.get(2), PostExplicitTimeAo.class, searchProcessor,
                postFeaturesMap);
        processFeatureCollection(userFeatureList.get(3), PostExplicitTimeAo.class, operateProcessor,
                postFeaturesMap);
        processFeatureCollection(userFeatureList.get(4), PostExplicitTimeAo.class, commentProcessor,
                postFeaturesMap);

        // 7.叠加计算
        List<PostHeatAo> postHeatAos = new ArrayList<>();
        for (Map.Entry<Long, List<ScoreDaysAo>> entry : postFeaturesMap.entrySet()) {
            Long postId = entry.getKey();
            List<ScoreDaysAo> scoreDaysAos = entry.getValue();
            PostHeatAo postHeatAo = rulePostHeat.execute(scoreDaysAos, postId);
            postHeatAos.add(postHeatAo);
        }

        // 8.热度从大到小排序
        postHeatAos.sort(
                (o1, o2) ->
                        o2.getHeatScore().compareTo(o1.getHeatScore())
        );
        return postHeatAos;
    }

    private void processPostFeatures(PostExplicitTimeAo feature,
                                     Map<Long, List<ScoreDaysAo>> postFeaturesMap) {
        for (PostExplicitPostScoreAo scoreAo : feature.getPostExplicitPostScoreAos()) {
            Long postId = scoreAo.getPostId();
            Long timestamp = scoreAo.getTimestamp();
            if (timestamp == null) {
                continue;
            }

            // 处理帖子post特征
            List<ScoreDaysAo> postFeatures = postFeaturesMap.computeIfAbsent(postId, k -> new ArrayList<>());
            ScoreDaysAo postScoreDaysAo = new ScoreDaysAo();
            postScoreDaysAo.setDays(getDays(timestamp));
            postScoreDaysAo.getScoreAo().setImplicitScore(scoreAo.getScore());
            postFeatures.add(postScoreDaysAo);
        }
    }

    @Override
    public PostHeatAo getPostHeat(Long postId) {
        String redisKey = UserActionRedisKey.POST_HEAT_LIST_REDIS_KEY_PREFIX;

        // 从 Redis 中获取热度分数
        Double heatScore = redissonService.zScore(redisKey, postId);

        // 如果热度分数为 null，返回 null 或者创建一个默认的 PostHeatAo
        if (heatScore == null) {
            return null; // 或者返回一个新的 PostHeatAo 例如 `new PostHeatAo(postId, 0)`
        }

        // 创建 PostHeatAo 对象并返回
        PostHeatAo postHeatAo = new PostHeatAo();
        postHeatAo.setPostId(postId);
        postHeatAo.setHeatScore(heatScore);
        return postHeatAo;
    }

    @Override
    public List<PostHeatAo> getPostHeats(List<Long> postIds) {
        String redisKey = UserActionRedisKey.POST_HEAT_LIST_REDIS_KEY_PREFIX;

        Map<Object, Double> scores = redissonService.zScores(redisKey, new ArrayList<>(postIds));

        List<PostHeatAo> postHeatAos = new ArrayList<>();
        for (Map.Entry<Object, Double> entry : scores.entrySet()) {
            Long postId = (Long) entry.getKey();
            Double heatScore = entry.getValue();
            PostHeatAo postHeatAo = new PostHeatAo();
            postHeatAo.setPostId(postId);
            postHeatAo.setHeatScore(heatScore);
            postHeatAos.add(postHeatAo);
        }
        return postHeatAos;
    }

    private int getDays(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long days = (currentTime - timestamp) / (1000L * 60 * 60 * 24);
        return (int) days;
    }

}
