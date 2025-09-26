package com.ai.medicine.domain.Do.neo4j.base;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * @author 13225
 * @date 2025/5/12 17:15
 */
@Data
public abstract class BaseNeo4jDo {
    @Id
    // 经过测试，id必须交给neo4j生成，如果需要userId则另外设置字段
    @GeneratedValue
    protected Long id;
    @Property("name")
    protected String name;
    public abstract String getNodeLabel();
}
