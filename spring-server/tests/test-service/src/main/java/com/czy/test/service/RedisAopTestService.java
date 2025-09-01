package com.czy.test.service;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/9/1 16:25
 */
public interface RedisAopTestService {
    String hitTest(@NotNull Long userId);

    String missTest(@NotNull Long userId);
}
