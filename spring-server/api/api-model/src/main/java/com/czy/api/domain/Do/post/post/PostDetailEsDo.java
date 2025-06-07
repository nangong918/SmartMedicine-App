package com.czy.api.domain.Do.post.post;

import com.czy.api.constant.es.FieldAnalyzer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/4/16 21:04
 * 朋友圈Details帖子Do
 * 存放在mongoDB + ES
 */
@Document(indexName = "post_detail", // 索引名 必须是小写
        shards = 1, // 默认索引分区数
        replicas = 0, // 每个分区的备份数
        refreshInterval = "-1" // 刷新间隔
)
@Data
public class PostDetailEsDo implements Serializable {
    // id；postDetails的id与postInfo的id一致
    @Id
    private Long id;
    // title；not null
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    private String title;
//    // content；not null
//    @Field(analyzer = FieldAnalyzer.IK_SMART, type = FieldType.Text)
//    private String content;

}
