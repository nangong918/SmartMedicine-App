package com.czy.api.domain.Do.user;

import json.BaseBean;
import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author 13225
 * @date 2025/5/6 17:49
 */
@Data
public class UserNeo4jDo implements BaseBean {
    @Id
    @Field("id")
    private Long id;
    @Field("account")
    private String account;
    @Field("name")
    private String name;
}
