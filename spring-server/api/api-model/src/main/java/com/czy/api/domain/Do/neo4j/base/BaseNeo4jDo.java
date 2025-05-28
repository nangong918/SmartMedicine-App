package com.czy.api.domain.Do.neo4j.base;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/12 17:15
 */
@Data
public abstract class BaseNeo4jDo {
    @Id
    @Field("id")
    private Long id;
    @Field("name")
    private String name;
    public abstract String getNodeLabel();
}
