package com.czy.api.domain.Do.neo4j;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/6 17:59
 */
@Data
@NodeEntity("症状")
public class SymptomsDo {
    // nodeLabel
    public static final String nodeLabel = "症状";
    @Id
    @Field("id")
    private Long id;
    @Field("name")
    private String name;
}
