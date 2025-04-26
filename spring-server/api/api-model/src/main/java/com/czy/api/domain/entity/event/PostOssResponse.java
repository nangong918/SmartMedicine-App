package com.czy.api.domain.entity.event;

import com.czy.api.constant.oss.OssResponseTypeEnum;
import com.czy.api.constant.oss.OssTaskTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/21 15:25
 */
@Data
public class PostOssResponse {
    // 用户id
    public Long userId;
    // userAccount 用于netty通知前端
    public String userAccount;
    // service id
    public String serviceId;
    // publish的雪花id// postId
    public Long publishId;
    // fileIds
    public List<Long> fileIds;
    // oss处理id;redis的雪花id
    public String fileRedisKey;
    // clusterLockPath
    public String clusterLockPath;
    // 当前时间
    public Long currentTime = System.currentTimeMillis();
    // oss响应类型
    public int ossResponseType = OssResponseTypeEnum.NULL.getCode();
    // oss操作类型：增删改查
    public int ossOperationType = OssTaskTypeEnum.NULL.getCode();
}
