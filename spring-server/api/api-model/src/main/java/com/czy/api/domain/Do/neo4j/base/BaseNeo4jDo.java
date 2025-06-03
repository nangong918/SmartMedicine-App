package com.czy.api.domain.Do.neo4j.base;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;

/**
 * @author 13225
 * @date 2025/5/12 17:15
 */
@Data
public abstract class BaseNeo4jDo {
    @Id
    @Property("id")
    private Long id;
    @Property("name")
    private String name;
    public abstract String getNodeLabel();
}
