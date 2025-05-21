package com.czy.api.domain.Do.neo4j;

import com.czy.api.domain.Do.neo4j.base.BaseNeo4jDo;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/6 17:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NodeEntity("post")
public class PostNeo4jDo extends BaseNeo4jDo implements BaseBean {
    // nodeLabel
    public static final String NODE_LABEL = "post";
    @Field("title")
    private String title;
    @Field("label")
    private String label;
    @Override
    public String getNodeLabel() {
        return NODE_LABEL;
    }
}
