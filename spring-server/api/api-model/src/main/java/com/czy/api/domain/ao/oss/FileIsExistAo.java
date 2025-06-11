package com.czy.api.domain.ao.oss;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/6/11 16:23
 */
@Data
public class FileIsExistAo implements BaseBean, Serializable {
    private Long userId;
    private String fileName;
    private String bucketName;
    private Long fileSize;
}
