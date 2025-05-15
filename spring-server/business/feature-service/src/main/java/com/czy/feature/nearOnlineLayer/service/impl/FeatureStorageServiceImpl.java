package com.czy.feature.nearOnlineLayer.service.impl;

import com.czy.api.constant.feature.FeatureTypeChanger;
import com.czy.api.constant.feature.PostTypeEnum;
import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.PostLabelNeo4jDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import com.czy.api.domain.ao.feature.NerFeatureScoreAo;
import com.czy.api.domain.ao.feature.PostExplicitPostScoreAo;
import com.czy.api.domain.ao.feature.PostExplicitTimeAo;
import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.feature.ScoreAo;
import com.czy.api.domain.ao.feature.UserEntityFeatureAo;
import com.czy.api.domain.ao.post.PostNerResult;
import com.czy.api.mapper.UserFeatureRepository;
import com.czy.feature.nearOnlineLayer.service.FeatureStorageService;
import com.czy.feature.nearOnlineLayer.service.PostFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/7 16:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeatureStorageServiceImpl implements FeatureStorageService {

    // post的更新是删除全部更新特征，但是user并不是，user是在原有的基础上添加
    private final UserFeatureRepository userFeatureRepository;
//    private final ChecksRepository checksRepository;
//    private final DepartmentsRepository departmentsRepository;
//    private final DiseaseRepository diseaseRepository;
//    private final DrugsRepository drugsRepository;
//    private final FoodsRepository foodsRepository;
//    private final PostRepository postRepository;
//    private final ProducersRepository producersRepository;
//    private final RecipesRepository recipesRepository;
//    private final SymptomsRepository symptomsRepository;

    private final PostFeatureService postFeatureService;

    private void saveUserRelation(UserFeatureNeo4jDo user) {
        UserFeatureNeo4jDo findUserDo = userFeatureRepository.findByName(user.getName());
        if (findUserDo == null){
            userFeatureRepository.save(user);
        }
    }

    @Override
    public void createRelationsUserWithEntities(UserFeatureNeo4jDo user, List<? extends BaseNeo4jDo> entities) {
        saveUserRelation(user);
        for (BaseNeo4jDo entity : entities) {
            try {
                String relationType = FeatureTypeChanger.nodeLabelToRelation(entity.getNodeLabel());

                // 存储关系
                userFeatureRepository.createUserEntityPostRelation(
                        user.getId(), entity.getNodeLabel(),
                        entity.getName(), relationType
                );
            } catch (Exception ignored) {
                // 因为存储的时候实体可能不存在，但是业务逻辑也不能在此创建，所以就直接continue
                continue;
            }
        }
    }


    @Override
    public void createRelationUserWithDiseases(UserFeatureNeo4jDo user, List<DiseaseDo> dos) {
        // 存储关系 (单独写因为DiseaseDo不是常规实体，可能存在拓展功能)
        createRelationsUserWithEntities(user, dos);
    }


    @Override
    public void createRelationUserWithPosts(UserFeatureNeo4jDo user, List<PostNeo4jDo> dos) {
        // 存储关系 (单独写因为PostNeo4jDo不是常规实体，可能存在拓展功能)
        createRelationsUserWithEntities(user, dos);
    }

    @Override
    public void deleteUserRelation(UserFeatureNeo4jDo user) {
        userFeatureRepository.deletePostWithRelations(user.getName());
    }

    @Override
    public void uploadUserEntityFeature(@NotNull PostFeatureAo postFeatureAo, Long userId) {
        // user-entity
        if (!CollectionUtils.isEmpty(postFeatureAo.getPostNerResultList())){
            for (PostNerResult postNerResult : postFeatureAo.getPostNerResultList()) {
                String keyWord = postNerResult.getKeyWord();
                String nerType = postNerResult.getNerType();
                userFeatureRepository.createUserEntityPostRelation(
                        userId,
                        FeatureTypeChanger.nerTypeToEntityLabel(nerType),
                        keyWord,
                        FeatureTypeChanger.nerTypeToUserRelationType(nerType)
                );
            }
        }
        // user-label
        PostTypeEnum postTypeEnum = PostTypeEnum.getByCode(postFeatureAo.getPostType());
        if (postFeatureAo.getPostType() != null && !postTypeEnum.equals(PostTypeEnum.OTHER)){
            userFeatureRepository.createUserEntityPostRelation(
                    userId,
                    PostLabelNeo4jDo.nodeLabel,
                    postTypeEnum.getName(),
                    UserFeatureRepository.RELS_USER_POST_LABEL
            );
        }
    }

    @Override
    public void saveUserEntityFeature(Long userId, UserEntityFeatureAo userEntityFeatureAo) {
        Map<String, NerFeatureScoreAo> nerFeatureScoreMap = userEntityFeatureAo.getNerFeatureScoreMap();
        // user-entity
        if (!CollectionUtils.isEmpty(nerFeatureScoreMap)){
            for (Map.Entry<String, NerFeatureScoreAo> entry : nerFeatureScoreMap.entrySet()) {
                String keyWord = entry.getKey();
                NerFeatureScoreAo nerFeatureScoreAo = entry.getValue();
                String nerType = nerFeatureScoreAo.getNerType();
                if (!nerFeatureScoreAo.isEmpty()) {
                    ScoreAo scoreAo = nerFeatureScoreAo.getScore();
                    if (!scoreAo.isEmpty()) {
                        userFeatureRepository.saveOrUpdateUserEntityRelation(
                                userId,
                                FeatureTypeChanger.nerTypeToEntityLabel(nerType),
                                keyWord,
                                FeatureTypeChanger.nerTypeToUserRelationType(nerType),
                                scoreAo.getClickTimes(),
                                scoreAo.getImplicitScore(),
                                scoreAo.getExplicitScore()
                        );
                    }
                }
            }
        }
        // user-label
        Map<Integer, ScoreAo> labelScoreMap = userEntityFeatureAo.getLabelScoreMap();
        if (!CollectionUtils.isEmpty(labelScoreMap)){
            for (Map.Entry<Integer, ScoreAo> entry : labelScoreMap.entrySet()) {
                Integer postType = entry.getKey();
                ScoreAo scoreAo = entry.getValue();
                if (!scoreAo.isEmpty()) {
                    userFeatureRepository.saveOrUpdateUserEntityRelation(
                            userId,
                            PostLabelNeo4jDo.nodeLabel,
                            PostTypeEnum.getByCode(postType).getName(),
                            UserFeatureRepository.RELS_USER_POST_LABEL,
                            scoreAo.getClickTimes(),
                            scoreAo.getImplicitScore(),
                            scoreAo.getExplicitScore()
                    );
                }
            }
        }
    }

    @Override
    public void saveUserExplicitFeature(Long userId, @NotNull PostExplicitTimeAo postExplicitTimeAo) {
        // user-post
        for (PostExplicitPostScoreAo postExplicitPostScoreAo : postExplicitTimeAo.getPostExplicitPostScoreAos()) {
            userFeatureRepository.saveOrUpdateUserPostRelation(
                    userId,
                    postExplicitPostScoreAo.getPostId(),
                    0,
                    0.0,
                    postExplicitPostScoreAo.getScore()
            );
            /// entity + label
            PostFeatureAo postFeatureAo = postFeatureService.getPostFeature(postExplicitPostScoreAo.getPostId());
            if (postFeatureAo == null){
                continue;
            }
            /// entity
            if (!CollectionUtils.isEmpty(postFeatureAo.getPostNerResultList())){
                for (PostNerResult postNerResult : postFeatureAo.getPostNerResultList()){
                    String keyWord = postNerResult.getKeyWord();
                    String nerType = postNerResult.getNerType();
                    userFeatureRepository.saveOrUpdateUserEntityRelation(
                            userId,
                            nerType,
                            keyWord,
                            FeatureTypeChanger.nerTypeToUserRelationType(nerType),
                            0,
                            0.0,
                            postExplicitPostScoreAo.getScore()
                    );
                }
            }
            /// label
            Integer label = postFeatureAo.getPostType();
            PostTypeEnum postTypeEnum = PostTypeEnum.getByCode(label);
            if (postTypeEnum == PostTypeEnum.OTHER){
                continue;
            }
            userFeatureRepository.saveOrUpdateUserEntityRelation(
                    userId,
                    PostLabelNeo4jDo.nodeLabel,
                    postTypeEnum.getName(),
                    UserFeatureRepository.RELS_USER_POST_LABEL,
                    0,
                    0.0,
                    postExplicitPostScoreAo.getScore()
            );
        }
//        // user-entity
//        for (PostExplicitEntityScoreAo postExplicitEntityScoreAo : postExplicitTimeAo.getPostExplicitEntityScoreAos()) {
//            userFeatureRepository.saveOrUpdateUserEntityRelation(
//                    userId,
//                    postExplicitEntityScoreAo.getEntityLabel(),
//                    postExplicitEntityScoreAo.getEntityName(),
//                    FeatureTypeChanger.nerTypeToUserRelationType(postExplicitEntityScoreAo.getEntityLabel()),
//                    0,
//                    0.0,
//                    postExplicitEntityScoreAo.getScore()
//            );
//        }
//        // user-label
//        for (PostExplicitLabelScoreAo postExplicitLabelScoreAo : postExplicitTimeAo.getPostExplicitLabelScoreAos()) {
//            PostTypeEnum postTypeEnum = PostTypeEnum.getByCode(postExplicitLabelScoreAo.getLabel());
//            if (Objects.equals(postExplicitLabelScoreAo.getLabel(), postTypeEnum.getCode())) {
//                continue;
//            }
//            userFeatureRepository.saveOrUpdateUserEntityRelation(
//                    userId,
//                    PostLabelNeo4jDo.nodeLabel,
//                    postTypeEnum.getName(),
//                    UserFeatureRepository.RELS_USER_POST_LABEL,
//                    0,
//                    0.0,
//                    postExplicitLabelScoreAo.getScore()
//            );
//        }
    }
}
