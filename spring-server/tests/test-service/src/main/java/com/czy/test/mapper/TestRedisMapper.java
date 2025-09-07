package com.czy.test.mapper;

import com.czy.test.domain.Do.TestDo;
import org.springframework.data.repository.CrudRepository;

/**
 * @author 13225
 * @date 2025/8/21 14:20
 */
// 这傻逼玩意没有设置redis过期时间的功能, 要他有屁用
public interface TestRedisMapper extends CrudRepository<TestDo, String> {
}
