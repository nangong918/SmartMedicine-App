package com.czy.feature.mapper;

import com.czy.api.domain.Do.neo4j.SymptomsDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface SymptomsRepository extends Neo4jRepository<SymptomsDo, Long> {
    SymptomsDo findByName(String name);
}
