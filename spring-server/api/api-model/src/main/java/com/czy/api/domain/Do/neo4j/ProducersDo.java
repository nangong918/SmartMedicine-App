package com.czy.api.domain.Do.neo4j;

import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author 13225
 * @date 2025/5/6 17:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("药企")
public class ProducersDo extends BaseNeo4jDo implements BaseBean {
    // nodeLabel
    public static final String nodeLabel = "药企";
    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }
}
