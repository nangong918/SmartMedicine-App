package com.ai.medicine.mapper.rels;

import com.ai.medicine.domain.Do.neo4j.SymptomsDo;
import com.ai.medicine.domain.Do.neo4j.base.BaseNeo4jMatchDo;
import com.ai.medicine.domain.Do.neo4j.rels.HasSymptomRelsDo;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 13225
 * @date 2025/9/25 11:22
 */
@Repository
public interface DiseaseSymptomRelationRepository extends Neo4jRepository<HasSymptomRelsDo, Long> {

    @Query("""
            MATCH (d:疾病)-[r:has_symptom]->(s:症状)
            WHERE d.name = $diseaseName
            RETURN r
            LIMIT 20
            """)
    List<HasSymptomRelsDo> findSymptomsRelsByDiseaseName(@Param("diseaseName") String diseaseName);

    /**
     * 根据疾病名称查询症状
     * @param diseaseName   疾病名称
     * @return  症状列表
     * {
     *   "identity": 32275,
     *   "labels": [
     *     "症状"
     *   ],
     *   "properties": {
     *     "name": "发热伴寒战"
     *   },
     *   "elementId": "32275"
     * }
     */
    @Query("""
            MATCH (d:疾病)-[:has_symptom]->(s:症状)
            WHERE d.name = $diseaseName
            RETURN s.name as name, id(s) as id
            LIMIT 20
            """)
    List<SymptomsDo> findSymptomsByDiseaseName(@Param("diseaseName") String diseaseName);

    /**
     * 根据症状名称列表查询相关疾病
     * 注意此处的2是代表疾病
     * @see com.ai.medicine.domain.constant.MedicalEntityEnum
     * @param symptomNames 症状名称列表
     * @return 疾病列表
     */
    @Query("""
        MATCH (d:疾病)-[:has_symptom]->(s:症状)
        WHERE s.name IN $symptomNames
        WITH d, COUNT(s) AS matchedSymptomCount
        WHERE matchedSymptomCount >= 1
        RETURN d.name as name, matchedSymptomCount as matchedSymptomCount, 2 as entityType
        ORDER BY matchedSymptomCount DESC
        LIMIT 20
        """)
    List<BaseNeo4jMatchDo> findDiseaseMatchsBySymptomNames(@Param("symptomNames") List<String> symptomNames);
}
