package com.czy.api.domain.Do.neo4j.rels;

import com.czy.api.domain.Do.neo4j.PostNeo4jDo;
import com.czy.api.domain.Do.neo4j.UserFeatureNeo4jDo;
import com.czy.api.mapper.UserFeatureRepository;
import lombok.Data;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * @author 13225
 * @date 2025/6/4 10:48
 */
@Data
@RelationshipEntity(UserFeatureRepository.RELS_USER_PUBLISH_POST)
public class UserPublishPostRelation {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private UserFeatureNeo4jDo user;

    @EndNode
    private PostNeo4jDo post;

}
