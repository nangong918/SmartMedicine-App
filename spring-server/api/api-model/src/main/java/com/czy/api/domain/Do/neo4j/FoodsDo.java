package com.czy.api.domain.Do.neo4j;

import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author 13225
 * @date 2025/5/6 17:57
 */


@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("食物")
public class FoodsDo extends BaseNeo4jDo {
    // nodeLabel
    public static final String nodeLabel = "食物";

    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }
}
