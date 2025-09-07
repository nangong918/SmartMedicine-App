package com.czy.test.domain.Do;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author 13225
 * @date 2025/8/21 14:19
 */
@RedisHash("test")
@Data
public class TestDo {
    @Id
    private Long id;
    private String name;
}
