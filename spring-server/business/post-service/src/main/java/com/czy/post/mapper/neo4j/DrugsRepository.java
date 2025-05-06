package com.czy.post.mapper.neo4j;

import com.czy.api.domain.Do.neo4j.DrugsDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface DrugsRepository extends Neo4jRepository<DrugsDo, Long> {
    DrugsDo findByName(String name);
}
