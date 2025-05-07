package com.czy.api.domain.Do.neo4j;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/7 15:03
 */
@Data
@NodeEntity("user")
public class UserFeatureNeo4jDo {
    @Id
    @Field("id")
    private Long id;
    @Field("account")
    private String account;
    @Field("name")
    private String name;
}
