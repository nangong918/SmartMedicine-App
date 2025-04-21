package com.czy.api.domain.entity.event;

import com.czy.api.constant.oss.OssResponseTypeEnum;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/21 15:25
 */
@Data
public class OssResponse {
    // 用户id
    public Long userId;
    // service id
    public String serviceId;
    // publish的雪花id
    public Long publishId;
    // oss处理id;redis的雪花id
    public String fileRedisKey;
    // clusterLockPath
    public String clusterLockPath;
    // 当前时间
    public Long currentTime = System.currentTimeMillis();
    // oss响应类型
    public int ossResponseType = OssResponseTypeEnum.NULL.getCode();
}
