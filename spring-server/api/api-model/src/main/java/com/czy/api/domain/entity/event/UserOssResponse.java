package com.czy.api.domain.entity.event;

import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/9 17:04
 */
@Data
public class UserOssResponse {
    // userId
    public Long userId;
    // phone
    public String phone;
    // clusterLockPath
    public String clusterLockPath;
    // fileIds
    public List<Long> fileIds;
    // oss处理id;redis的雪花id
    public String fileRedisKey;
    // 当前时间
    public Long currentTime = System.currentTimeMillis();
    // oss响应类型
    public int ossResponseType = OssResponseTypeEnum.NULL.getCode();
    // oss操作类型：增删改查
    public int ossOperationType = OssTaskTypeEnum.NULL.getCode();
}
