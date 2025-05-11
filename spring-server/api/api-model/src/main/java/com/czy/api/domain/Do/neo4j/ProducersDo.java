package com.czy.api.domain.Do.neo4j;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/6 17:58
 */
@Data
@NodeEntity("药企")
public class ProducersDo {
    // nodeLabel
    public static final String nodeLabel = "药企";
    @Id
    @Field("id")
    private Long id;
    @Field("name")
    private String name;
}
