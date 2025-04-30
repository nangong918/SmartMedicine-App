package com.czy.search.mapper.es;

import com.czy.api.domain.Do.test.TestSearchEsDo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 18:14
 */
public interface TestSearchEsMapper extends ElasticsearchRepository<TestSearchEsDo, Long> {

    // TODO 需要了解DSL；每个关键词的意思（其实这已经属于计算机中的边缘技能了，新的框架新的东西，没有必要）
    List<TestSearchEsDo> findBySearchNameLike(String searchName);

}
