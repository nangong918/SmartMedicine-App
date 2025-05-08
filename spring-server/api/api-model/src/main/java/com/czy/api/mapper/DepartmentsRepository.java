package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface DepartmentsRepository extends Neo4jRepository<DepartmentsDo, Long> {
    DepartmentsDo findByName(String name);
}
