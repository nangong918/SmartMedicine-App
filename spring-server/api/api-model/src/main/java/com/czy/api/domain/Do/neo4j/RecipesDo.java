package com.czy.api.domain.Do.neo4j;

import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author 13225
 * @date 2025/5/6 17:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("菜谱")
public class RecipesDo extends BaseNeo4jDo {
    // nodeLabel
    public static final String nodeLabel = "菜谱";
    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }
}
