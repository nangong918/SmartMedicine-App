package com.czy.user.mapper.es;

import com.czy.api.domain.Do.user.UserDo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/16 9:57
 */
public interface UserEsMapper extends ElasticsearchRepository<UserDo, Long> {

    // 查找
    List<UserDo> findByUserNameContaining(String userName);

}
