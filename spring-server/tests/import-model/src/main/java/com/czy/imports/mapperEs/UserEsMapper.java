package com.czy.imports.mapperEs;

import com.czy.api.domain.Do.user.UserDo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 13225
 * @date 2025/4/16 9:57
 */
public interface UserEsMapper extends ElasticsearchRepository<UserDo, Long> {

}
