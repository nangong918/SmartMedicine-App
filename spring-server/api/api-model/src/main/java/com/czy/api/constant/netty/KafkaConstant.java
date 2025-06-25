package com.czy.api.constant.netty;

/**
 * @author 13225
 * @date 2025/6/19 18:07
 */
public interface KafkaConstant {

    String KAFKA_TOPIC = "kafka-topic";

    interface GroupId{
        // 隐性特征：Point埋点
        String Feature_Implicit = "feature-implicit-";
        // 显性特征：Action直接行为
        String Feature_Explicit = "feature-explicit-";
    }

    interface Topic {
        // 评论
        String Comment = "comment";
        // 埋点
        String Point = "point";
        // 帖子操作行为
        String Post_Operation = "post_operation";
        // 搜索
        String Search = "search";
    }
}
