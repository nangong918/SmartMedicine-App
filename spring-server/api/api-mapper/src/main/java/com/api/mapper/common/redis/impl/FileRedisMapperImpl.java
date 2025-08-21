package com.api.mapper.common.redis.impl;

import com.api.mapper.common.redis.FileRedisMapper;
import com.utils.redisson.service.RedissonService;
import domain.FileResAo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/21 13:51
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FileRedisMapperImpl implements FileRedisMapper {

    private final RedissonService redissonService;

    @Override
    public boolean insertFileResAo(@NotNull String redisKey, @NotNull FileResAo fileResAo, @Nullable Long expireTime) {
        return redissonService.setObjectByJson(redisKey, fileResAo, expireTime);
    }

    @Override
    public boolean deleteFileResAo(@NotNull String redisKey) {
        return redissonService.deleteObject(redisKey);
    }

    @Override
    public FileResAo getFileResAo(@NotNull String redisKey) {
        return redissonService.getObjectFromJson(redisKey, FileResAo.class);
    }

    @Override
    public boolean updateFileResAo(@NotNull String redisKey, @NotNull FileResAo fileResAo, @Nullable Long expireTime) {
        return redissonService.setObjectByJson(redisKey, fileResAo, expireTime);
    }
}
