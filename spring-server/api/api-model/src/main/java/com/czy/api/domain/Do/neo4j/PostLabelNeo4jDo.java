package com.czy.api.domain.Do.neo4j;

import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * @author 13225
 * @date 2025/5/12 9:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("post_label")
public class PostLabelNeo4jDo extends BaseNeo4jDo {
    // nodeLabel
    public static final String nodeLabel = "post_label";
    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }
}
