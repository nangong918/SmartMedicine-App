package com.czy.api.mapper;

import com.czy.api.domain.Do.neo4j.SymptomsDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/6 18:16
 */

@Repository
public interface SymptomsRepository extends Neo4jRepository<SymptomsDo, Long> {
    SymptomsDo findByName(String name);

    /**
     * 根据症状名称
     MATCH (s:症状 {name: '发烧'})<-[r:has_symptom]-(d:疾病)
     RETURN d.name AS diseaseName
     LIMIT 10
     * @param symptomName   症状名称
     * @return 满足的疾病name list
     */
    @Query("MATCH (s:症状 {name: $symptomName})<-[r:has_symptom]-(d:疾病) " +
            "RETURN d.name AS diseaseName")
    List<Map<String, Object>> findDiseasesBySymptom(@Param("symptomName") String symptomName);

    /**
     * 通过症状名称list查询满足全部这些症状的疾病
     WITH ['发烧', '头痛'] AS symptomNames
     MATCH (s:症状) WHERE s.name IN symptomNames
     WITH collect(s) AS symptoms
     MATCH (d:疾病)
     WHERE ALL(symptom IN symptoms
        WHERE (d)-[:has_symptom]-(symptom))
     RETURN d.name AS diseaseName
     LIMIT 10
     * @param symptomNames  症状名称 list
     * @return  满足全部症状的疾病name list
     */
    @Query("MATCH (d:疾病) " +
            "WHERE ALL(symptom IN $symptomNames " +
            "   WHERE (d)-[:has_symptom]-(s:症状 {name: symptom})) " +
            "RETURN d.name AS diseaseName")
    List<Map<String, Object>> findDiseasesByAllSymptoms(@Param("symptomNames") List<String> symptomNames);
}
