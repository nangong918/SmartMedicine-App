package com.api.mapper.common.redis;

import domain.FileResAo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author 13225
 * @date 2025/8/21 13:49
 */
public interface FileRedisMapper {

    /// FileResAo (object, json)
    boolean insertFileResAo(@NotNull String redisKey, @NotNull FileResAo fileResAo, @Nullable Long expireTime);
    boolean deleteFileResAo(@NotNull String redisKey);
    FileResAo getFileResAo(@NotNull String redisKey);
    boolean updateFileResAo(@NotNull String redisKey, @NotNull FileResAo fileResAo, @Nullable Long expireTime);
}
