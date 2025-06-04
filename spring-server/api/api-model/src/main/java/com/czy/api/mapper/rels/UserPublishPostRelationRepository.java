package com.czy.api.mapper.rels;

import com.czy.api.domain.Do.neo4j.rels.UserPublishPostRelation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/6/4 11:04
 */
@Repository
public interface UserPublishPostRelationRepository extends Neo4jRepository<UserPublishPostRelation, Long> {
}
