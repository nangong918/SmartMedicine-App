package com.czy.api.domain.entity.event;

import com.czy.api.constant.oss.OssTaskTypeEnum;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/18 23:40
 */
@Data
public class OssTask {
    public Long userId;
    // 唯一id，有了这个其他都能查到
    public Long ossFileId;
    // userId + fileName可以查到
    public String fileName;
    // userId + fileStorageName + bucketName可以查到
    public String fileStorageName;
    public String bucketName;
    // 当前时间
    public Long currentTime = System.currentTimeMillis();
    // 执行类型
    public int ossTaskType = OssTaskTypeEnum.NULL.getCode();
}
