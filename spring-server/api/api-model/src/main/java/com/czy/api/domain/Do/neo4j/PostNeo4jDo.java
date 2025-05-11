package com.czy.api.domain.Do.neo4j;

import json.BaseBean;
import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/6 17:46
 */
@Data
@NodeEntity("post")
public class PostNeo4jDo implements BaseBean {
    // nodeLabel
    public static final String NODE_LABEL = "post";
    @Id
    @Field("id")
    private Long id;
    @Field("title")
    private String title;
    @Field("name")
    private String name;
    @Field("label")
    private String label;
}
