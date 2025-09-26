package com.ai.medicine.domain.Do.neo4j;

import com.ai.medicine.domain.Do.neo4j.base.BaseNeo4jDo;
import com.ai.medicine.domain.constant.MedicalEntityEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * @author 13225
 * @date 2025/5/6 17:57
 */


@EqualsAndHashCode(callSuper = true)
@Data
@Node("食物")
public class FoodsDo extends BaseNeo4jDo {
    // nodeLabel
    public static final String nodeLabel = "食物";

    @Override
    public String getNodeLabel() {
        return MedicalEntityEnum.FOOD.getDescription();
    }
}
