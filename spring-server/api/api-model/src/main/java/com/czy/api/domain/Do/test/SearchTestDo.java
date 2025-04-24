package com.czy.api.domain.Do.test;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author 13225
 * @date 2025/4/24 17:31
 */
@Document("search_test")
@Data
public class SearchTestDo {
    @Id
    private Long id;
    private String searchName;
}
