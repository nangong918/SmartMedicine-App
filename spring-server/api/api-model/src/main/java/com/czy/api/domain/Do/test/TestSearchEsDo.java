package com.czy.api.domain.Do.test;

import com.czy.api.constant.es.FieldAnalyzer;
import json.BaseBean;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author 13225
 * @date 2025/4/24 17:31
 */
@Data
@org.springframework.data.elasticsearch.annotations.Document(
        indexName = "test_search", // 索引名 必须是小写
        shards = 1, // 默认索引分区数
        replicas = 0, // 每个分区的备份数
        refreshInterval = "-1" // 刷新间隔
)
public class TestSearchEsDo implements BaseBean {
    @Id
    private Long id;
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, searchAnalyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    private String searchName;
}
