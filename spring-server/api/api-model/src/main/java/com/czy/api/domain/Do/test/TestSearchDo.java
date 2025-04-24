package com.czy.api.domain.Do.test;

import json.BaseBean;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 13225
 * @date 2025/4/24 17:31
 */
@Document("test_search")
@Data
public class TestSearchDo implements BaseBean {
    @Id
    private Long id;
    private String searchName;
}
