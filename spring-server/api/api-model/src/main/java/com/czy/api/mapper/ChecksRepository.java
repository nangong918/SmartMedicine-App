package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.ChecksDo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface ChecksRepository extends Neo4jRepository<ChecksDo, Long> {
    String CQL_USER_CHECKS = UserFeatureRepository.RELS_USER_CHECKS + "|" + PostRepository.RELS_POST_CHECKS + "|cure_department";

    @Query("MATCH (n:检查) " +
            "WHERE n.name = $name " +
            "RETURN n")
    ChecksDo findByName(@Param("name")String name);
}
