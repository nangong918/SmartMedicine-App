package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.DepartmentsDo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface DepartmentsRepository extends Neo4jRepository<DepartmentsDo, Long> {
    String CQL_USER_DEPARTMENTS = UserFeatureRepository.RELS_USER_DEPARTMENTS + "|" + PostRepository.RELS_POST_DEPARTMENTS + "|cure_department";

    @Query("MATCH (n:科室) " +
            "WHERE n.name = $name " +
            "RETURN n")
    DepartmentsDo findByName(@Param("name")String name);
}
