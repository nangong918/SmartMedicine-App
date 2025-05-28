package com.czy.api.domain.Do.neo4j;

import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/7 15:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("user")
public class UserFeatureNeo4jDo extends BaseNeo4jDo {
    // nodeLabel
    public static final String nodeLabel = "user";
    @Field("account")
    private String account;
    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }
}
