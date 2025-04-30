package com.czy.search.mapper;

import com.czy.api.domain.Do.test.TestSearchDo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/24 17:40
 */

@Mapper
public interface TestSearchMapper {

    // 插入
    int insert(TestSearchDo testSearchDo);

    // 删除
    int delete(Long id);

    // id查找
    TestSearchDo selectById(Long id);

    // 模糊查找
    List<TestSearchDo> selectByLikeName(String likeName);

}
