package com.api.mapper.post.es;

import com.czy.api.domain.Do.post.post.PostDetailEsDo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 13225
 * @date 2025/4/17 21:26
 */
@Repository
public interface PostDetailEsMapper extends ElasticsearchRepository<PostDetailEsDo, Long> {
    // 通过标题搜索
    Page<PostDetailEsDo> findByTitleContaining(String title, Pageable pageable);
//    // 通过内容搜索
//    Page<PostDetailEsDo> findByContentContaining(String content, Pageable pageable);
}
