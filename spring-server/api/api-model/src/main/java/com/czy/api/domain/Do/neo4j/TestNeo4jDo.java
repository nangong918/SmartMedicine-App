package com.czy.api.domain.Do.neo4j;

import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * @author 13225
 * @date 2025/6/3 17:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("test")
public class TestNeo4jDo extends BaseNeo4jDo implements BaseBean {
    // nodeLabel
    public static final String nodeLabel = "test";
    @Property("account")
    private String account;
    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }
}
