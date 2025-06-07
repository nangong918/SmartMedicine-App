package com.czy.api.domain.Do.post.post;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/5/28 11:03
 * 多对多关系需要创建单独的表
 * 这种日志数据应该存储在Hive中而不是MySQL
 */
@Data
public class UserPostBrowseDo implements Serializable {
    @Id
    private Long id;
    // 索引；查询user全部浏览的post
    private Long userId;
    // 索引；查询post被多哪些人浏览
    private Long postId;
    // not null
    private Long timestamp;
}
