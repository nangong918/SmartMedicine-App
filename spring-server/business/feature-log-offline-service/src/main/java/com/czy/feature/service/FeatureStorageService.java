package com.czy.feature.service;

import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import com.czy.api.domain.ao.feature.PostExplicitTimeAo;
import com.czy.api.domain.ao.feature.PostFeatureAo;
import com.czy.api.domain.ao.feature.UserEntityFeatureAo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/7 16:34
 */
public interface FeatureStorageService {

    //  createRelationUserWithDiseases
    void createRelationUserWithDiseases(UserFeatureNeo4jDo user, List<DiseaseDo> dos);
    // createRelationsUserWithEntities
    void createRelationsUserWithEntities(UserFeatureNeo4jDo user, List<? extends BaseNeo4jDo> entities);
    // createRelationUserWithPosts
    void createRelationUserWithPosts(UserFeatureNeo4jDo user, List<PostNeo4jDo> dos);
    // deleteUserRelation
    void deleteUserRelation(UserFeatureNeo4jDo user);

    /**
     * 更新用户的点击事件：user-entity、label关系中的点击增加
     * @param postFeatureAo     post特征信息
     * @param userId            用户id
     */
    void uploadUserEntityFeature(@NotNull PostFeatureAo postFeatureAo, Long userId);

    /**
     * 保存用户浏览事件：user-entity、label关系中的分值增加
     * @param userId                用户id
     * @param userEntityFeatureAo   用户分数特征信息
     */
    void saveUserEntityFeature(Long userId, @NotNull UserEntityFeatureAo userEntityFeatureAo);

    /**
     * 保存用户显性特征信息
     * @param userId                用户id
     * @param postExplicitTimeAo    显性特征信息
     */
    void saveUserExplicitFeature(Long userId, @NotNull PostExplicitTimeAo postExplicitTimeAo);
}
