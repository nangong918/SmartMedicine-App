package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.ChecksDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface ChecksRepository extends Neo4jRepository<ChecksDo, Long> {
    ChecksDo findByName(String name);
}
