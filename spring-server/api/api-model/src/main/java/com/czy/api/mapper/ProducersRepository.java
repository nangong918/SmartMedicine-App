package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.ProducersDo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface ProducersRepository extends Neo4jRepository<ProducersDo, Long> {
    String CQL_USER_PRODUCERS = UserFeatureRepository.RELS_USER_PRODUCERS + "|" + PostRepository.RELS_POST_PRODUCERS + "|cure_department";

    @Query("MATCH (n:药企) " +
            "WHERE n.name = $name " +
            "RETURN n")
    ProducersDo findByName(@Param("name") String name);
}
