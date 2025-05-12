package com.czy.api.domain.Do.neo4j;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/12 9:56
 */
@Data
@NodeEntity("post_label")
public class PostLabelNeo4jDo {
    // nodeLabel
    public static final String nodeLabel = "post_label";
    @Id
    @Field("id")
    private Long id;
    @Field("name")
    private String name;
}
