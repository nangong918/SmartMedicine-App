package com.ai.medicine.domain.Do.neo4j.rels;

import com.ai.medicine.domain.Do.neo4j.base.BaseNeo4jDo;
import com.ai.medicine.domain.constant.MedicalRelationEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

/**
 * @author 13225
 * @date 2025/9/25 11:11
 */
@EqualsAndHashCode(callSuper = true)
@RelationshipProperties
@Data
public class BelongsToRelsDo extends BaseNeo4jDo {
    @Override
    public String getNodeLabel() {
        return MedicalRelationEnum.BELONGS_TO.getDescription();
    }
}
