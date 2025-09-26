package com.ai.medicine.domain.Do.neo4j.base;

import com.ai.medicine.domain.constant.MedicalEntityEnum;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/9/25 11:45
 */
@Data
public class BaseNeo4jMatchDo {
    /**
     * 匹配的数量
     */
    private Integer matchedSymptomCount = 0;
    /**
     * 名称
     */
    private String name;
    /**
     * 实体类型
     * @see com.ai.medicine.domain.constant.MedicalEntityEnum
     */
    private Integer entityType = MedicalEntityEnum.UNKNOWN.getCode();

    public String toDocumentString(){
        MedicalEntityEnum medicalEntityEnum = MedicalEntityEnum.getByCode(entityType);
        if (medicalEntityEnum.equals(MedicalEntityEnum.UNKNOWN) || medicalEntityEnum.equals(MedicalEntityEnum.OTHER)){
            return medicalEntityEnum.getDescription();
        }
        return "[" +
                "name: " + name +
                "matchedSymptomCount: " + matchedSymptomCount +
                "entityType: " + medicalEntityEnum.getDescription() +
                "]";
    }
}
