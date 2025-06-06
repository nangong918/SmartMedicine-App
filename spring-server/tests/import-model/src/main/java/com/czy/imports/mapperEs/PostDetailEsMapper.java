package com.czy.imports.mapperEs;

import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 13225
 * @date 2025/4/17 21:26
 */
public interface PostDetailEsMapper extends ElasticsearchRepository<PostDetailEsDo, Long> {
    // 删除全部
}
