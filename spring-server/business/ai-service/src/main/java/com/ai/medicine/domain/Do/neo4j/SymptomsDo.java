package com.ai.medicine.domain.Do.neo4j;

import com.ai.medicine.domain.Do.neo4j.base.BaseNeo4jDo;
import com.ai.medicine.domain.constant.MedicalEntityEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * @author 13225
 * @date 2025/5/6 17:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Node("症状")
public class SymptomsDo extends BaseNeo4jDo {

    @Override
    public String getNodeLabel() {
        return MedicalEntityEnum.SYMPTOM.getDescription();
    }
}
