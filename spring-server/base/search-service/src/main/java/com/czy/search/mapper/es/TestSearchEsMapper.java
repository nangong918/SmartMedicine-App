package com.czy.search.mapper.es;

import com.czy.api.domain.Do.test.TestSearchEsDo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 18:14
 */
public interface TestSearchEsMapper extends ElasticsearchRepository<TestSearchEsDo, Long> {

    List<TestSearchEsDo> findBySearchNameContaining(String searchName);

}
