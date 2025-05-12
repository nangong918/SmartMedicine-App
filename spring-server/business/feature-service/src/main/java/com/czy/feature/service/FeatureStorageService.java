package com.czy.feature.service;

import com.czy.api.domain.Do.neo4j.DiseaseDo;
import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;

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
}
